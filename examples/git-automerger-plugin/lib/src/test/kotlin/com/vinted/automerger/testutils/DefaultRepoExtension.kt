package com.vinted.automerger.testutils

import org.junit.jupiter.api.extension.ExtensionContext

class DefaultRepoExtension : RepoExtension() {
    override fun beforeEach(context: ExtensionContext) {
        super.beforeEach(context)

        with(git) {
            createRelease("8.9")
            createRelease("8.10")
            checkoutMaster()
        }
    }
}
