package com.vinted.automerger.testutils

import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File

open class RepoExtension : BeforeEachCallback, AfterEachCallback {
    lateinit var path: File
        private set

    lateinit var git: Git
        private set

    override fun beforeEach(context: ExtensionContext) {
        path = File.createTempFile("AutomergerTestRepository-${System.currentTimeMillis()}", "")

        if (!path.delete()) {
            throw RuntimeException("Failed delete $path file")
        }

        git = Git.init().setDirectory(path).setInitialBranch("master").call()

        with(git) {
            commit().setAllowEmpty(true).setMessage("init commit").call()
        }
    }

    override fun afterEach(context: ExtensionContext) {
        if (!path.deleteRecursively()) {
            System.err.println("Failed delete temporally created repo folder $path")
        }
    }
}
