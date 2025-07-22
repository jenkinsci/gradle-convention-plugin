package com.vinted.automerger.plugin

import com.vinted.automerger.AutoMergerBuilder
import com.vinted.slf4j.adapter.SLF4JOutputStreamAdapter
import hudson.FilePath
import hudson.Launcher
import hudson.model.Descriptor
import hudson.model.Run
import hudson.model.TaskListener
import hudson.remoting.VirtualChannel
import hudson.tasks.BuildStepMonitor
import hudson.tasks.BuildStepMonitor.NONE
import hudson.tasks.Builder
import hudson.util.FormValidation
import jenkins.tasks.SimpleBuildStep
import org.jenkinsci.remoting.RoleChecker
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter
import org.kohsuke.stapler.QueryParameter
import java.io.File

class GitAutomergerStep @DataBoundConstructor constructor() : Builder(), SimpleBuildStep {

    @set:DataBoundSetter
    var detailConflictReport: Boolean = false

    @set:DataBoundSetter
    var limitAuthorsInDetailReport: Int = 3
    @set:DataBoundSetter
    var limitCommitsInDetailReport: Int = 3

    @set:DataBoundSetter
    var releaseBranchPattern: String = "release/%"
    @set:DataBoundSetter
    var mergeRules: List<JenkinsMergeRule>? = null
    @set:DataBoundSetter
    var logLevel: LogLevel = LogLevel.WARN
    @set:DataBoundSetter
    var remoteName: String = "origin"
    @set:DataBoundSetter
    var checkoutFromRemote: Boolean = false


    fun doCheckLimitAuthorsInDetailReport(@QueryParameter number: Int): FormValidation {
        return if (number < 1) {
            FormValidation.error("You must let report at least one author")
        } else {
            FormValidation.ok()
        }
    }

    fun doCheckLimitCommitsInDetailReport(@QueryParameter number: Int): FormValidation {
        return if (number < 0) {
            FormValidation.error("Comments limit must be 0 or any positive number")
        } else {
            FormValidation.ok()
        }
    }

    override fun getDescriptor(): Descriptor<Builder> = DESCRIPTOR

    override fun getRequiredMonitorService(): BuildStepMonitor = NONE

    fun doCheckReleaseBranchPattern(@QueryParameter releaseBranchPattern: String): FormValidation {
        if (releaseBranchPattern.trim().isEmpty()) {
            return FormValidation.error("releaseBranchPattern can't be empty.")
        } else if (!releaseBranchPattern.contains("%")) {
            return FormValidation.error("You must use '%' which denotes version number in branch name")
        }
        return FormValidation.ok()
    }

    fun doCheckRemoteName(@QueryParameter remoteName: String): FormValidation {
        return if (remoteName.isEmpty()) {
            FormValidation.error("Remote name can't be empty. In most cases you should use 'origin'")
        } else {
            FormValidation.ok()
        }
    }

    @Deprecated("Overrides deprecated member")
    @Suppress("DEPRECATION")
    override fun perform(run: Run<*, *>, workspace: FilePath, launcher: Launcher, listener: TaskListener) {
        val builder = AutoMergerBuilder()
            .releaseBranchPattern(releaseBranchPattern)
            .checkoutFromRemote(checkoutFromRemote)
            .remoteName(remoteName)
            .detailConflictReport(detailConflictReport)
            .limitAuthorsInDetailReport(limitAuthorsInDetailReport)
            .limitCommitsInDetailReport(limitCommitsInDetailReport)

        mergeRules.orEmpty().map(JenkinsMergeRule::toMergeConfig).forEach {
            builder.addMergeConfig(it)
        }

        workspace.act(AutoMergerExecutor(builder, logLevel, listener))
    }

    /**
     * @param logLevel logger is not serializable, so all info are provided to construct proper logger
     * @param listener logger is not serializable, so all info are provided to construct proper logger
     */
    class AutoMergerExecutor(
        val autoMergerBuilder: AutoMergerBuilder,
        var logLevel: LogLevel,
        val listener: TaskListener
    ) : FilePath.FileCallable<Boolean> {

        override fun checkRoles(checker: RoleChecker?) {
            throw SecurityException()
        }

        override fun invoke(file: File, channel: VirtualChannel?): Boolean {
            val logger = SLF4JOutputStreamAdapter(listener.logger, logLevel.levelInt)

            autoMergerBuilder
                .logger(logger)
                .pathToRepo(file)
                .build()
                .automerge()

            return true
        }
    }

    companion object {
        @JvmStatic
        val DESCRIPTOR = GitAutomergerDescriptor()
    }
}
