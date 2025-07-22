package com.vinted.automerger

import com.vinted.automerger.testutils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.RegisterExtension

class AutoMergerTest {
    @RegisterExtension
    @JvmField
    val repo: RepoExtension = DefaultRepoExtension()

    private lateinit var defaultBuilder: AutoMergerBuilder

    @BeforeEach
    fun setUp() {
        defaultBuilder = AutoMergerBuilder().pathToRepo(repo.path)
    }

    @Test
    fun workingDirIsDirty_crash() {
        with (repo.git) {
            val randomFile = repo.path.resolve("foo.txt")
            randomFile.writeText("Random text")
        }

        val fixture = defaultBuilder.build()

        assertThrows<IllegalStateException> {
            fixture.automerge()
        }
    }
}
