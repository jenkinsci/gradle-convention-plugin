package com.vinted.automerger

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.blame.BlameResult
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit

class ConflictDetailsCollector(
    val git: Git,
    private val mergeResult: MergeResult,
    private val authorsLimit: Int,
    private val commitsLimit: Int
) {
    private val pathToRepo = git.repository.directory.parentFile

    fun collect(): String {
        applyWorkaround()

        try {
            val allCommits = mutableMapOf<RevCommit, Int>()

            for ((filename, _) in mergeResult.conflicts) {
                val blameResult = git.blame()
                    .setFilePath(filename)
                    .setFollowFileRenames(true)
                    .call()

                blameResult.computeAll()

                calculateConflictLines(filename, blameResult).forEach { (commit, lines) ->
                    allCommits.merge(commit, lines, Int::plus)
                }
            }

            return allCommits.asIterable()
                .fold(mutableMapOf<PersonIdent, List<Pair<RevCommit, Int>>>()) { acc, (commit, nr) ->
                    acc.apply { compute(commit.authorIdent) { _, list -> (list.orEmpty()) + (commit to nr) } }
                }
                .toList()
                .sortedByDescending { it.second.sumOf { it.second } }
                .joinToString(separator = "\n", limit = authorsLimit) { (author, list) ->
                    val totalLines = list.sumOf { it.second }
                    val summary = list.joinToString(separator = "\n", limit = commitsLimit) { (commit, total) ->
                        val sha = commit.name.take(8)
                        "  - Commit $sha causes $total lines of conflicts with message: ${commit.shortMessage.take(80)}"
                    }
                    "Changes of ${author.name} cause total $totalLines lines of conflicts:\n$summary"
                }


        } finally {
            //resets mess of [applyWorkaround]
            git.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD^").call()
        }
    }

    /**
     * Workaround for bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=434330
     */
    private fun applyWorkaround() {
        for ((filename, _) in mergeResult.conflicts) {
            git.add().addFilepattern(filename).call()
        }
        git.commit().setMessage("Temp commit for proper blaming").call()
    }

    private fun calculateConflictLines(filename: String, blameResult: BlameResult): Map<RevCommit, Int> {
        var isInChangeChunk = false

        //commit to number
        val commits = mutableMapOf<RevCommit, Int>()

        for ((line, nr) in pathToRepo.resolve(filename).readLines().zip(0..Int.MAX_VALUE)) {
            if (line == "=======") continue

            if (line.startsWith("<<<<<<<")) {
                isInChangeChunk = true
                continue
            }

            if (line.startsWith(">>>>>>>")) {
                isInChangeChunk = false
                continue
            }

            if (isInChangeChunk) {
                val commit = blameResult.getSourceCommit(nr)
                commits.compute(commit) { _, oldValue -> (oldValue ?: 0) + 1 }
            }
        }

        return commits.toList().sortedByDescending { it.second }.toMap()
    }
}
