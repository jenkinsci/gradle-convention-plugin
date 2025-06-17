package jpi

import JenkinsPluginExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.gradle.kotlin.dsl.withType
import org.jenkinsci.gradle.plugins.jpi.JpiExtension
import org.jenkinsci.gradle.plugins.jpi.core.PluginDeveloper
import java.io.File

public object JpiPluginConfiguration {

    private val logger = Logging.getLogger(JpiPluginConfiguration::class.java)

    public fun configureJpiPlugin(project: Project) {
        project.afterEvaluate {
            configureJpiExtension(it)
            configureJpiTasks(it)
        }
    }

    private fun configureJpiExtension(project: Project) {
        val jpi = project.extensions.findByType(JpiExtension::class.java)
        val jenkins = project.extensions.findByType(JenkinsPluginExtension::class.java)

        if (jpi == null || jenkins == null) {
            logger.warn("Skipping JPI configuration: required extensions not found.")
            return
        }

        mapExtensionProperties(jenkins, jpi)
    }

    private fun mapExtensionProperties(jenkins: JenkinsPluginExtension, jpi: JpiExtension) {

        jenkins.pluginId.orNull?.let { jpi.shortName = it }

        jenkins.humanReadableName.orNull?.let { jpi.humanReadableName.set(it) }

        jenkins.minimumJenkinsVersion.orNull?.let {
            jpi.jenkinsVersion.set(it)
            jpi.compatibleSinceVersion = it
        }

        jenkins.usePluginFirstClassLoader.orNull?.let { jpi.pluginFirstClassLoader = it }

        jenkins.sandboxed.orNull?.let { jpi.sandboxed.set(it) }

        jenkins.maskedClassesFromCore.orNull?.takeIf { it.isNotEmpty() }?.let { jpi.maskClasses = it.joinToString(" ") }

        jenkins.generateTests.orNull?.let { jpi.generateTests.set(it) }

        jenkins.requireEscapeByDefaultInJelly.orNull?.let { jpi.requireEscapeByDefaultInJelly.set(it) }

        // Metadata
        jenkins.homePage.orNull?.let { jpi.homePage.set(it) }

        jenkins.extension.orNull?.let { jpi.extension.set(it) }

        jenkins.gitHub.orNull?.let { jpi.gitHub.set(it) }

        jenkins.scmTag.orNull?.let { jpi.scmTag.set(it) }

        jenkins.incrementalsRepoUrl.orNull?.let { jpi.incrementalsRepoUrl.set(it.toString()) }

        jenkins.generatedTestClassName.orNull?.let { jpi.generatedTestClassName.set(it) }

        jenkins.testJvmArguments.orNull?.let { jpi.testJvmArguments.set(it) }

        jenkins.workDir.orNull?.asFile?.let {
            jpi.workDir = it
        }

        jenkins.repoUrl.orNull?.let { jpi.snapshotRepoUrl = it.toString() }

        jenkins.snapshotRepoUrl.orNull?.let { jpi.snapshotRepoUrl = it.toString() }

        jenkins.configurePublishing.orNull?.let { jpi.configurePublishing = it }

        jenkins.configureRepositories.orNull?.let { jpi.configureRepositories = it }

        jenkins.pluginDevelopers.orNull?.forEach { dev ->
            jpi.developers(Action { developers ->
                developers.developer(Action { developer ->
                    developer.id.set(dev.id)
                    developer.name.set(dev.name)
                    developer.email.set(dev.email)
                    developer.url.set(dev.portfolioUrl.toString())
                    developer.organization.set(dev.organization)
                    developer.organizationUrl.set(dev.organizationUrl.toString())
                    developer.roles.set(dev.roles)
                    developer.timezone.set(dev.timezone)
                })
            })
        }

        jenkins.pluginLicenses.orNull?.forEach { lic ->
            jpi.licenses(Action { licenses ->
                licenses.license(Action { license ->
                    license.name.set(lic.name)
                    license.url.set(lic.url.toString())
                    license.distribution.set(lic.distribution)
                    license.comments.set(lic.comments)
                })
            })
        }

        jenkins.gitVersion.orNull?.let {
            jpi.gitVersion.versionFormat.set(it.versionFormat)
            jpi.gitVersion.abbrevLength.set(it.abbrevLength)
            jpi.gitVersion.allowDirty.set(it.allowDirty)
            jpi.gitVersion.gitRoot.set(it.gitRoot)
            jpi.gitVersion.sanitize.set(it.sanitize)
            jpi.gitVersion.outputFile.set(it.outputFile)
            jpi.gitVersion.versionPrefix.set(it.versionPrefix)
        }
    }

    private fun configureJpiTasks(project: Project) {
        project.tasks.findByName("jpi")?.apply {
            group = "build"
            description = "Builds the Jenkins plugin (.jpi file)"
        }
        project.tasks.findByName("server")?.apply {
            group = "jenkins"
            description = "Runs a local jenkins server for testing"
        }
    }
}
