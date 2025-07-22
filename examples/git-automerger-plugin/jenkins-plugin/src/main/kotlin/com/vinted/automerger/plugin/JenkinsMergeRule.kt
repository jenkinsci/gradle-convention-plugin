package com.vinted.automerger.plugin

import com.vinted.automerger.config.Resolution
import com.vinted.automerger.config.MergeRule
import hudson.Extension
import hudson.model.AbstractDescribableImpl
import hudson.util.FormValidation
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

/**
 * This class is only wrapper to [MergeRule] for Jenkins data-bounding.
 */
@Suppress("DEPRECATION")
data class JenkinsMergeRule @DataBoundConstructor constructor(
    val path: String,
    val resolution: Resolution,
) : AbstractDescribableImpl<JenkinsMergeRule>() {

    fun toMergeConfig() = MergeRule(path, resolution)

    fun doCheckPath(@QueryParameter path: String): FormValidation {
        if (path.isEmpty()) {
            return FormValidation.error("Path can't be empty")
        }

        return FormValidation.ok()
    }

    @Extension
    class DescriptorImpl : hudson.model.Descriptor<JenkinsMergeRule>() {
        override fun getDisplayName(): String {
            return "Merge conflict config"
        }
    }
}
