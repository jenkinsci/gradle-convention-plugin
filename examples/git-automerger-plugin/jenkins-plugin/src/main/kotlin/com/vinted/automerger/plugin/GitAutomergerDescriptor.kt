package com.vinted.automerger.plugin

import hudson.Extension
import hudson.model.AbstractProject
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Builder
import net.sf.json.JSONObject
import org.jenkinsci.Symbol
import org.kohsuke.stapler.StaplerRequest

@Extension
@Symbol("gitAutomerger")
class GitAutomergerDescriptor : BuildStepDescriptor<Builder>(GitAutomergerStep::class.java) {
    override fun isApplicable(jobType: Class<out AbstractProject<*, *>>): Boolean {
        return true
    }

    @Deprecated("Overrides deprecated member")
    @Suppress("DEPRECATION")
    override fun newInstance(req: StaplerRequest?, formData: JSONObject): Builder {
        if (req == null) throw AssertionError("req should be always not-null")

        return req.bindJSON(GitAutomergerStep::class.java, formData)
    }

    override fun getDisplayName(): String {
        return "Git automerge"
    }

    @Deprecated("Overrides deprecated member")
    @Suppress("DEPRECATION")
    override fun configure(req: StaplerRequest, json: JSONObject): Boolean {
        save()
        return true
    }
}
