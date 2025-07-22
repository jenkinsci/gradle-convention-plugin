package com.vinted.automerger

import com.vinted.automerger.testutils.DefaultRepoExtension
import com.vinted.automerger.testutils.RepoExtension
import com.vinted.automerger.testutils.setCurrentStageTo
import com.vinted.automerger.testutils.checkoutMaster
import org.eclipse.jgit.revwalk.RevCommit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

class ConflictReportingTest {
    @RegisterExtension
    @JvmField
    val repo: RepoExtension = DefaultRepoExtension()

    private lateinit var defaultBuilder: AutoMergerBuilder

    private val testData = File("src/test/testData/ConflictReportingTest")

    private val author1 = "Author1"
    private val author2 = "Author2"
    private val author3 = "Author3"

    lateinit var author1Commit: RevCommit
    lateinit var author2Commit: RevCommit
    lateinit var author3Commit: RevCommit

    @BeforeEach
    fun setUp() {
        defaultBuilder = AutoMergerBuilder().pathToRepo(repo.path).detailConflictReport(true)

        with(repo.git) {
            setCurrentStageTo(testData.resolve("initial"))
            add().addFilepattern(".").call()
            author1Commit = commit().setMessage("Initial").setAuthor(author1, "$author1@gmail.com").call()

            checkout().setCreateBranch(true).setName("release/1.1").call()
            setCurrentStageTo(testData.resolve("change-b"))
            add().addFilepattern(".").call()
            author3Commit = commit().setMessage("Change b").setAuthor(author3, "$author3@gmail.com").call()

            checkoutMaster()
            setCurrentStageTo(testData.resolve("change-a"))
            add().addFilepattern(".").call()
            author2Commit = commit().setMessage("Change a").setAuthor(author2, "$author2@gmail.com").call()
        }
    }

    @Test
    fun test() {
        val message = """
            Changes of Author3 cause total 8 lines of conflicts:
              - Commit ${author3Commit.shortName()} causes 8 lines of conflicts with message: Change b
            Changes of Author2 cause total 6 lines of conflicts:
              - Commit ${author2Commit.shortName()} causes 6 lines of conflicts with message: Change a
            Changes of Author1 cause total 2 lines of conflicts:
              - Commit ${author1Commit.shortName()} causes 2 lines of conflicts with message: Initial
        """.trimIndent()

        val exception = assertThrows<UnresolvedConflictException>(message) {
            defaultBuilder.build().automerge()
        }

        assertEquals(message, exception.message)
    }

    fun RevCommit.shortName() = name.take(8)
}
