package jpi

import JenkinsConventionExtension
import JenkinsPluginBridge
import JenkinsPluginExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.jenkinsci.gradle.plugins.jpi.JpiExtension

public class JpiPluginBridge : JenkinsPluginBridge {

    private val logger = Logging.getLogger(JpiPluginBridge::class.java)

    override fun applyJpiPlugin(project: Project) {

        if (!super.isTestEnvironment(project)) {
            try {
                if (!project.plugins.hasPlugin(JenkinsPluginBridge.JPI2_PLUGIN_ID)) {
                    project.plugins.apply(JenkinsPluginBridge.JPI2_PLUGIN_ID)
                    logger.info("Applied JPI2 Plugin to project: ${project.name}")
                } else {
                    logger.info("JPI2 plugin already applied to project: ${project.name}")
                }
            } catch (e: Exception) {
                logger.warn("Failed to apply JPI2 to plugin: ${e.message}. This may be expected in test environments or if plugin is not available.")
            }
        } else {
            logger.info("Skipping the JPI2 plugin application in test environment")
        }
    }

    override fun configureJpiPlugin(project: Project, convention: JenkinsConventionExtension) {

        val jpiExtension = getJpiExtension(project) ?: return
        mapExtensionProperties(convention, jpiExtension)

        logger.debug("Successfully configured JPI plugin from convention extension")
    }

    override fun validateConfiguration(project: Project, convention: JenkinsConventionExtension): List<String> {

        val errors = mutableListOf<String>()

        if (!convention.pluginId.isPresent || convention.pluginId.get().isBlank()) {
            errors.add("Plugin ID must be specified and non-empty")
        }

        if (!convention.minimumJenkinsVersion.isPresent) {
            errors.add("Minimum Jenkins version must be specified")
        } else {
            val version = convention.minimumJenkinsVersion.get()
            if (!super.isValidJenkinsVersion(version)) {
                errors.add("Invalid Jenkins version format: $version")
            }
        }

        if (convention.pluginId.isPresent) {
            val pluginId = convention.pluginId.get()
            if (!isValidPluginId(pluginId)) {
                errors.add("Plugin ID $pluginId contains invalid characters. Use only lowercase letters, numbers and hyphens.")
            }
        }

        if (convention.pluginDevelopers.isPresent && convention.pluginDevelopers.get().isEmpty()) {
            logger.warn("No developers specified for plugin. Consider adding developer information.")
        }

        return errors
    }

    override fun getJpiExtension(project: Project): JpiExtension? {
        return project.extensions.findByName("jpi") as? JpiExtension
    }

    private fun mapExtensionProperties(convention: JenkinsConventionExtension, jpiExtension: JpiExtension) {

        convention.pluginId.orNull?.let { jpiExtension.shortName = it }

        convention.humanReadableName.orNull?.let { jpiExtension.humanReadableName.set(it) }

        convention.minimumJenkinsVersion.orNull?.let {
            jpiExtension.jenkinsVersion.set(it)
            jpiExtension.compatibleSinceVersion = it
        }

        convention.usePluginFirstClassLoader.orNull?.let { jpiExtension.pluginFirstClassLoader = it }

        convention.sandboxed.orNull?.let { jpiExtension.sandboxed.set(it) }

        convention.maskedClassesFromCore.orNull?.takeIf { it.isNotEmpty() }
            ?.let { jpiExtension.maskClasses = it.joinToString(" ") }

        convention.generateTests.orNull?.let { jpiExtension.generateTests.set(it) }

        convention.requireEscapeByDefaultInJelly.orNull?.let { jpiExtension.requireEscapeByDefaultInJelly.set(it) }

        // Metadata
        convention.homePage.orNull?.let { jpiExtension.homePage.set(it) }

        convention.extension.orNull?.let { jpiExtension.extension.set(it) }

        convention.gitHubUrl.orNull?.let { jpiExtension.gitHub.set(it) }

        convention.scmTag.orNull?.let { jpiExtension.scmTag.set(it) }

        convention.incrementalsRepoUrl.orNull?.let { jpiExtension.incrementalsRepoUrl.set(it.toString()) }

        convention.generatedTestClassName.orNull?.let { jpiExtension.generatedTestClassName.set(it) }

        convention.testJvmArguments.orNull?.let { jpiExtension.testJvmArguments.set(it) }

        convention.workDir.orNull?.asFile?.let {
            jpiExtension.workDir = it
        }

        convention.repoUrl.orNull?.let { jpiExtension.snapshotRepoUrl = it.toString() }

        convention.snapshotRepoUrl.orNull?.let { jpiExtension.snapshotRepoUrl = it.toString() }

        convention.configurePublishing.orNull?.let { jpiExtension.configurePublishing = it }

        convention.configureRepositories.orNull?.let { jpiExtension.configureRepositories = it }

        convention.pluginDevelopers.orNull?.forEach { dev ->
            jpiExtension.developers(Action { developers ->
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

        convention.pluginLicenses.orNull?.forEach { lic ->
            jpiExtension.licenses(Action { licenses ->
                licenses.license(Action { license ->
                    license.name.set(lic.name)
                    license.url.set(lic.url.toString())
                    license.distribution.set(lic.distribution)
                    license.comments.set(lic.comments)
                })
            })
        }


        convention.gitVersion.orNull?.let {
            jpiExtension.gitVersion.versionFormat.set(it.versionFormat)
            jpiExtension.gitVersion.abbrevLength.set(it.abbrevLength)
            jpiExtension.gitVersion.allowDirty.set(it.allowDirty)
            jpiExtension.gitVersion.gitRoot.set(it.gitRoot)
            jpiExtension.gitVersion.sanitize.set(it.sanitize)
            jpiExtension.gitVersion.outputFile.set(it.outputFile)
            jpiExtension.gitVersion.versionPrefix.set(it.versionPrefix)
        }
    }

    private fun isValidPluginId(pluginId: String): Boolean {
        val pattern = Regex("^[a-z0-9]+(?:-[a-z0-9]+)*$")
        return pattern.matches(pluginId) && !pluginId.contains("jenkins") && !pluginId.contains("plugin")
    }
}
