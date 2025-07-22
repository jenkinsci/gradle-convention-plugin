package com.vinted.automerger.testutils

import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.Assertions.assertEquals

fun Git.assertLatestCommitMessages(branchName: String, vararg messages: String) {
    val realMessages = commitMessageList(branchName, messages.count())
    assertEquals(messages.toSet(), realMessages.take(messages.size).toSet())
}
