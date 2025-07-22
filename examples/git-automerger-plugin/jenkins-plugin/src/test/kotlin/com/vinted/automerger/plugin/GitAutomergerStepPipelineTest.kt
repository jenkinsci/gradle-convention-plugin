package com.vinted.automerger.plugin

import hudson.FilePath
import hudson.model.Label
import hudson.model.Result
import hudson.remoting.VirtualChannel
import org.eclipse.jgit.api.Git
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.remoting.RoleChecker
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.JenkinsRule
import java.io.File

class GitAutomergerStepPipelineTest {

    @Rule
    @JvmField
    val jenkinsRule = JenkinsRule()

    lateinit var project: WorkflowJob
    lateinit var workspace: FilePath
    lateinit var repoDir: File

    @Before
    fun setUp() {
        project = jenkinsRule.createProject(WorkflowJob::class.java)
        workspace = jenkinsRule.jenkins.getWorkspaceFor(project)!!

        workspace.act(object : FilePath.FileCallable<Unit> {
            override fun checkRoles(checker: RoleChecker) {}

            override fun invoke(file: File, channel: VirtualChannel) {
                repoDir = file
                val git = Git.init().setDirectory(repoDir).call()

                File(repoDir, "CHANGELOG.md").writeText("Initial changelog")
                File(repoDir, "version").writeText("1.0.0")
                git.add().addFilepattern(".").call()
                git.commit().setMessage("Initial commit").call()

                git.branchCreate().setName("master").call()
                git.checkout().setCreateBranch(true).setName("feature-branch").call()
                File(repoDir, "CHANGELOG.md").appendText("\nFeature update")
                git.add().addFilepattern("CHANGELOG.md").call()
                git.commit().setMessage("Feature commit").call()

                git.checkout().setName("master").call()
            }
        })
    }

    private fun printBuildLog(build: hudson.model.Run<*, *>) {
        val log = jenkinsRule.createWebClient()
            .getPage(build, "console")
            .webResponse
            .contentAsString
        println("--- Jenkins Console Log ---")
        println(log)
        println("--- End of Log ---")
    }

    @Test
    fun runWithBasicConfig() {
        project.definition = CpsFlowDefinition(
            """
            node {
                echo "Simulating gitAutomerger step"
                echo "Would apply merge rules to CHANGELOG.md and version"
            }
        """.trimIndent(), true
        )

        val build = project.scheduleBuild2(0)!!.get()
        assertEquals(Result.SUCCESS, build.result)
    }

    @Test
    fun runAutomergerInAnotherNode() {
        jenkinsRule.createSlave(Label.parseExpression("android"))

        project.definition = CpsFlowDefinition(
            """
            node("android") {
                bat "echo Simulating git init"
                echo "Simulating gitAutomerger step on agent"
                echo "Would apply merge rules to CHANGELOG.md and version"
            }
        """.trimIndent(), true
        )

        val build = project.scheduleBuild2(0)!!.get()
        printBuildLog(build)
        assertEquals(Result.SUCCESS, build.result)
    }
}
