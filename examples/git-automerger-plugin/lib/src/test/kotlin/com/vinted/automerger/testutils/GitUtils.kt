package com.vinted.automerger.testutils

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.filter.RevFilter
import java.io.File

fun Git.checkoutBranch(branch: String) {
    checkout().setName(branch).call()
}

fun Git.checkoutMaster() {
    // Handle both old "master" and new "main" default branch names
    val branches = branchList().call().map { it.name }
    when {
        branches.any { it == "refs/heads/master" } -> checkoutBranch("master")
        branches.any { it == "refs/heads/main" } -> checkoutBranch("main")
        else -> {
            // If neither exists, create a master branch from the current HEAD
            checkout().setCreateBranch(true).setName("master").call()
        }
    }
}

fun Git.createRelease(version: String) {
    createBranchWithCommit("release/$version", "Release $version")
}

fun Git.createBranchWithCommit(branch: String, commitMessage: String) {
    checkoutMaster()
    checkout().setCreateBranch(true).setName(branch).call()
    commit().setAllowEmpty(true).setMessage(commitMessage).call()
    checkoutMaster()
}

fun Git.commitAll(message: String) {
    add().addFilepattern(".").call()
    commit().setMessage(message).call()
}

fun Git.commitMessageList(branchName: String, maxCount: Int = Int.MAX_VALUE): List<String> {
    val branch = branchList().call().find { it.name == "refs/heads/$branchName" }
    branch ?: throw AssertionError("Branch $branchName not found")

    return log()
        .setMaxCount(maxCount)
        .add(branch.objectId)
        .setRevFilter(RevFilter.NO_MERGES)
        .call()
        .map { it.shortMessage }
}

fun Git.setCurrentStageTo(dir: File) {
    rm().addFilepattern("*").call()

    //copy all files from source
    dir.copyRecursively(repository.directory.parentFile, true)
}
