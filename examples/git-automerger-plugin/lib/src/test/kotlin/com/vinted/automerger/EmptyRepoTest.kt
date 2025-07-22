package com.vinted.automerger

import com.vinted.automerger.testutils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class EmptyRepoTest {
    @RegisterExtension
    @JvmField
    val repo: RepoExtension = DefaultRepoExtension()

    private lateinit var defaultBuilder: AutoMergerBuilder

    @BeforeEach
    fun setUp() {
        defaultBuilder = AutoMergerBuilder().pathToRepo(repo.path)
    }

    @Test
    fun defaultCase() {
        val fixture = defaultBuilder.build()

        fixture.automerge()

        with(repo.git) {
            assertLatestCommitMessages("release/8.9", "Release 8.9")
            assertLatestCommitMessages("release/8.10", "Release 8.10", "Release 8.9")
            assertLatestCommitMessages("master", "Release 8.10", "Release 8.9")
        }
    }

    @Test
    fun thereIsNothingNewToMerge() {
        val fixture = defaultBuilder.build()
        fixture.automerge()

        fixture.automerge()

        with(repo.git) {
            assertLatestCommitMessages("release/8.9", "Release 8.9")
            assertLatestCommitMessages("release/8.10", "Release 8.10", "Release 8.9")
            assertLatestCommitMessages("master", "Release 8.10", "Release 8.9")
        }
    }

    @Test
    fun addAdditionalCommitToLowestPoint() {
        val fixture = defaultBuilder.build()
        fixture.automerge()

        with(repo.git) {
            checkoutBranch("release/8.9")
            commit().setMessage("Foo bar").setAllowEmpty(true).call()
            checkoutMaster()
        }

        fixture.automerge()

        with(repo.git) {
            assertLatestCommitMessages("release/8.9", "Foo bar", "Release 8.9")
            assertLatestCommitMessages("release/8.10", "Foo bar", "Release 8.9", "Release 8.10")
            assertLatestCommitMessages("master", "Foo bar", "Release 8.9", "Release 8.10", "init commit")
        }
    }

    @Test
    fun randomReleaseBranches() {
        val fixture = defaultBuilder.build()

        with(repo.git) {
            createBranchWithCommit("release/8.9-foo", "Foo")
            createBranchWithCommit("bar/8.9", "bar")
        }

        fixture.automerge()

        with(repo.git) {
            assertLatestCommitMessages("master", "Release 8.10")
        }
    }
}
