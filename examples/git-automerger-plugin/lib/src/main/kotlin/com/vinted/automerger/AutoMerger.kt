package com.vinted.automerger

import com.vinted.automerger.resolver.Resolver
import com.vinted.automerger.utils.checkNotEmpty
import com.vinted.automerger.utils.checkNotNull
import org.apache.maven.artifact.versioning.ComparableVersion
import org.eclipse.jgit.api.*
import org.eclipse.jgit.lib.BranchTrackingStatus
import org.eclipse.jgit.lib.ConfigConstants
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.merge.MergeStrategy

class AutoMerger(autoMergerBuilder: AutoMergerBuilder) {
    private val releaseBranchPattern = autoMergerBuilder.releaseBranchPattern
        .checkNotEmpty("Pattern must be set. For example 'release/%'")

    private val mergeConfig = autoMergerBuilder.mergeConfigs.toList()

    private val pathToRepo = autoMergerBuilder.pathToRepo
        .checkNotNull("Path must be set")

    private val logger = autoMergerBuilder.logger

    private val remoteName = autoMergerBuilder.remoteName
    private val checkoutFromRemote = autoMergerBuilder.checkoutFromRemote
    private val detailConflictReport = autoMergerBuilder.detailConflictReport
    private val limitAuthorsInDetailReport = autoMergerBuilder.limitAuthorsInDetailReport
    private val limitCommitsInDetailReport = autoMergerBuilder.limitCommitsInDetailReport

    private val git: Git

    private val branchPatternRegex = releaseBranchPattern.replace("%", "([0-9.]+)").plus("$").toRegex()

    init {
        if (!pathToRepo.exists()) {
            throw IllegalArgumentException("Repository doesn't exist on $pathToRepo")
        }

        if (!pathToRepo.isDirectory) {
            throw IllegalArgumentException("Path $pathToRepo must be directory to repo")
        }

        if (!pathToRepo.resolve(".git").isDirectory) {
            throw IllegalArgumentException(".git was not found inside $pathToRepo")
        }

        git = Git.open(pathToRepo)
        logger.debug("Open git repo at $pathToRepo")
    }

    fun automerge() {
        checkWorkingDir()

        val allReleaseBranches = allReleaseBranches()

        if (allReleaseBranches.size == 1) {
            logger.warn("Only applicable ${allReleaseBranches.first().name} branch was found. Nothing to merge")
            return
        }

        if (allReleaseBranches.isEmpty()) {
            logger.warn("Repository is missing 'master' and other applicable branches.")
            return
        }

        for ((from, to) in allReleaseBranches.windowed(2)) {
            logger.debug("Merge {} -> {}", from.name, to.name)
            with(git) {
                checkout().setName(to.name).call()
                val result = merge().include(repository.resolve(from.name)).setStrategy(MergeStrategy.RECURSIVE).call()

                try {
                    if (result.mergeStatus.isSuccessful) {
                        logger.debug("Merge was without conflict")
                    } else {
                        logger.debug("Conflict! Try to resolve them")

                        for ((fileName, _) in result.conflicts) {
                            logger.debug("> Conflict for file: $fileName")

                            if (!tryResolveConflict(fileName)) {
                                if (detailConflictReport) {
                                    throw collectDetailReport(result)
                                } else {
                                    throw UnresolvedConflictException("There is no defined conflict solver for $fileName")
                                }
                            }
                            git.add().addFilepattern(fileName).call()
                        }

                        git.commit().call()
                    }
                } catch (e: Exception) {
                    reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call()
                    throw e
                }
            }
        }
    }

    private fun checkWorkingDir() {
        val status = git.status().call()
        if (!status.isClean) {
            logger.error("Working space is not clean")

            val dirtyFiles = status.added + status.removed + status.changed + status.untracked
            logger.debug("Dirty files: ${dirtyFiles.joinToString(", ")}")

            throw IllegalStateException("Working space is not clean")
        }
    }

    /**
     * @return true is conflict was resolved
     */
    private fun tryResolveConflict(fileName: String): Boolean {
        val mode = mergeConfig.find { it.path == fileName } ?: return false
        val currentFile = pathToRepo.resolve(fileName)
        val tmpFile = pathToRepo.resolve("$fileName.tmp")

        if (tmpFile.exists()) {
            throw IllegalStateException("Can't create tmp file for merging: $tmpFile")
        }

        logger.debug("Solving $fileName")
        Resolver(currentFile.inputStream(), tmpFile.outputStream(), mode.resolution).resolve()

        logger.debug("Conflict solved")
        tmpFile.copyTo(currentFile, overwrite = true)
        tmpFile.delete()

        return true
    }

    private fun collectDetailReport(result: MergeResult): Throwable {
        val message = ConflictDetailsCollector(
            git = git,
            mergeResult = result,
            authorsLimit = limitAuthorsInDetailReport,
            commitsLimit = limitCommitsInDetailReport
        ).collect()

        return UnresolvedConflictException(message)
    }

    private fun allReleaseBranches(): List<Ref> {
        if (checkoutFromRemote) {
            logger.info("Checkout from remote $remoteName")

            val gitRefPath = "refs/remotes/$remoteName/"
            val localBranches = git.branchList().call().map { it.name.removePrefix("refs/heads/") }
            git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
                .filter { it.name.startsWith(gitRefPath) }
                .forEach { ref ->
                    val branchName = ref.name.removePrefix(gitRefPath)

                    if (!branchName.matches(branchPatternRegex) && !isDefaultBranch(branchName)) {
                        return@forEach
                    }

                    logger.info("Checkout ${ref.name} -> $branchName")

                    if (localBranches.contains(branchName)) {
                        logger.debug("Local branch '$branchName' was found and it is used, try to pull")

                        git.checkout()
                            .setName(branchName)
                            .setCreateBranch(false)
                            .call()

                        setTracking(branchName)
                        if (!canFastForward(branchName)) {
                            throw java.lang.IllegalStateException("Local branch '$branchName' diverged from remote")
                        }

                        val result = git.pull()
                            .setRemote(remoteName)
                            .setRemoteBranchName(branchName)
                            .setFastForward(MergeCommand.FastForwardMode.FF_ONLY)
                            .call()

                        if (!result.isSuccessful) {
                            error("Failed to pull $branchName")
                        }
                    } else {
                        logger.debug("Local branch '$branchName' was not found. Do checkout for it")
                        git.checkout()
                            .setName(branchName)
                            .setCreateBranch(true)
                            .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                            .setStartPoint(ref.name)
                            .call()
                    }
                }
        }

        // Get all local branches
        val allBranches = git.branchList().call()

        // Get release branches and sort them by version
        val releaseBranches = allBranches
            .mapNotNull { ref ->
                val name = ref.name.removePrefix("refs/heads/")
                val matches = branchPatternRegex.matchEntire(name)
                if (matches != null) {
                    val version = ComparableVersion(matches.groups[1]!!.value)
                    ref to version
                } else {
                    null
                }
            }
            .sortedBy { it.second }
            .map { it.first }

        // Get the default branch (master or main)
        val defaultBranch = allBranches.find {
            isDefaultBranch(it.name.removePrefix("refs/heads/"))
        }

        // Return release branches followed by default branch
        // The order should be: lowest version -> highest version -> master
        return if (defaultBranch != null) {
            releaseBranches + defaultBranch
        } else {
            releaseBranches
        }
    }

    private fun canFastForward(branchName: String): Boolean {
        val bts: BranchTrackingStatus = BranchTrackingStatus.of(git.repository, branchName)
        return bts.aheadCount == 0
    }

    private fun setTracking(branchName: String) {
        val config = git.repository.config;
        config.setString(
            ConfigConstants.CONFIG_BRANCH_SECTION,
            branchName,
            ConfigConstants.CONFIG_KEY_REMOTE, remoteName
        )
        config.setString(
            ConfigConstants.CONFIG_BRANCH_SECTION,
            branchName,
            ConfigConstants.CONFIG_KEY_MERGE,
            Constants.R_HEADS + branchName
        )
        config.save()
    }

    private fun isDefaultBranch(branchName: String): Boolean {
        return branchName == "master" || branchName == "main"
    }
}
