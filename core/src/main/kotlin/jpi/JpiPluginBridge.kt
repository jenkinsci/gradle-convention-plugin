package jpi

import JenkinsConventionExtension
import JenkinsPluginBridge
import JenkinsPluginExtension
import internal.ValidationUtils
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.jenkinsci.gradle.plugins.jpi.JpiExtension
import kotlin.math.PI

public class JpiPluginBridge : JenkinsPluginBridge {

    override fun applyJpiPlugin(project: Project) {

        if (super.isTestEnvironment(project)) {
            project.logger.info("Skipping the JPI2 plugin application in test environment")
            return
        }

        try {
            with(project.plugins) {
                if (!hasPlugin(JenkinsPluginBridge.JPI2_PLUGIN_ID)) {
                    apply(JenkinsPluginBridge.JPI2_PLUGIN_ID)
                    project.logger.info("Applied JPI2 Plugin to project: ${project.name}")
                } else {
                    project.logger.info("JPI2 plugin already applied to project: ${project.name}")
                }
            }
        } catch (e: Exception) {
            throw IllegalStateException(
                "Failed to apply JPI2 to plugin: ${e.message}. Ensure the plugin is available in the build environment.",
                e
            )
        }
    }

    override fun configureJpiPlugin(project: Project, convention: JenkinsConventionExtension) {

        val jpiExtension = getJpiExtension(project) ?: run {
            project.logger.debug("JPI extension not found, skipping configuration")
            return
        }

        mapExtensionProperties(convention, jpiExtension)

        project.logger.debug("Successfully configured JPI plugin from convention extension")
    }

    override fun validateConfiguration(project: Project, convention: JenkinsConventionExtension): List<String> {
        val errors = mutableListOf<String>()

        val pluginId = convention.pluginId.orNull
        if (pluginId.isNullOrBlank()) {
            errors.add("Plugin ID must be specified and non-empty")
        } else if (!ValidationUtils.isValidPluginId(pluginId)) {
            errors.add("Plugin ID '$pluginId' contains invalid characters. Use only lowercase letters, numbers or hyphens.")
        }

        // Jenkins Version Validation
        val jenkinsVersion = convention.minimumJenkinsVersion.orNull
        if (jenkinsVersion.isNullOrBlank()) {
            errors.add("Minimum Jenkins version must be specified")
        } else if (!ValidationUtils.isValidJenkinsVersion(jenkinsVersion)) {
            errors.add("Invalid Jenkins version format: $jenkinsVersion")
        }

        convention.pluginDevelopers.orNull?.let { developers ->
            if (developers.isEmpty()) {
                project.logger.warn("No developers specified for plugin. Consider adding developer information.")
            }
        }

        return errors
    }

    override fun getJpiExtension(project: Project): JpiExtension? {
        return project.extensions.findByName("jpi") as? JpiExtension
    }

//    private fun mapExtensionProperties(convention: JenkinsConventionExtension, jpiExtension: JpiExtension) {
//
//        convention.pluginId.orNull?.let { jpiExtension.shortName = it }
//
//        convention.humanReadableName.orNull?.let { jpiExtension.humanReadableName.set(it) }
//
//        convention.minimumJenkinsVersion.orNull?.let {
//            jpiExtension.jenkinsVersion.set(it)
//            jpiExtension.compatibleSinceVersion = it
//        }
//
//        convention.usePluginFirstClassLoader.orNull?.let { jpiExtension.pluginFirstClassLoader = it }
//
//        convention.sandboxed.orNull?.let { jpiExtension.sandboxed.set(it) }
//
//        convention.maskedClassesFromCore.orNull?.takeIf { it.isNotEmpty() }
//            ?.joinToString(" ")?.also { jpiExtension.maskClasses = it }
//
//        convention.generateTests.orNull?.let { jpiExtension.generateTests.set(it) }
//
//        convention.generatedTestClassName.orNull?.let { jpiExtension.generatedTestClassName.set(it) }
//
//        convention.requireEscapeByDefaultInJelly.orNull?.let { jpiExtension.requireEscapeByDefaultInJelly.set(it) }
//
//        convention.homePage.orNull?.let { jpiExtension.homePage.set(it) }
//
//        convention.extension.orNull?.let { jpiExtension.extension.set(it) }
//
//        convention.gitHubUrl.orNull?.let { jpiExtension.gitHub.set(it) }
//
//        convention.scmTag.orNull?.let { jpiExtension.scmTag.set(it) }
//
//        convention.incrementalsRepoUrl.orNull?.let { jpiExtension.incrementalsRepoUrl.set(it.toString()) }
//
//        convention.testJvmArguments.orNull?.let { jpiExtension.testJvmArguments.set(it) }
//
//        convention.workDir.orNull?.asFile?.let {
//            jpiExtension.workDir = it
//        }
//
//        convention.repoUrl.orNull?.let { jpiExtension.repoUrl = it.toString() }
//
//        convention.snapshotRepoUrl.orNull?.let { jpiExtension.snapshotRepoUrl = it.toString() }
//
//        convention.configurePublishing.orNull?.let { jpiExtension.configurePublishing = it }
//
//        convention.configureRepositories.orNull?.let { jpiExtension.configureRepositories = it }
//
//        convention.pluginDevelopers.orNull?.forEach { dev ->
//            jpiExtension.developers(Action { developers ->
//                developers.developer(Action { developer ->
//                    developer.id.set(dev.id)
//                    developer.name.set(dev.name)
//                    developer.email.set(dev.email)
//                    developer.url.set(dev.portfolioUrl.toString())
//                    developer.organization.set(dev.organization)
//                    developer.organizationUrl.set(dev.organizationUrl.toString())
//                    developer.roles.set(dev.roles)
//                    developer.timezone.set(dev.timezone)
//                })
//            })
//        }
//
//        convention.pluginLicenses.orNull?.forEach { lic ->
//            jpiExtension.licenses(Action { licenses ->
//                licenses.license(Action { license ->
//                    license.name.set(lic.name)
//                    license.url.set(lic.url.toString())
//                    license.distribution.set(lic.distribution)
//                    license.comments.set(lic.comments)
//                })
//            })
//        }
//
//
//        convention.gitVersion.orNull?.let { config ->
//            jpiExtension.gitVersion.apply {
//                versionFormat.set(config.versionFormat)
//                abbrevLength.set(config.abbrevLength)
//                allowDirty.set(config.allowDirty)
//                gitRoot.set(config.gitRoot)
//                sanitize.set(config.sanitize)
//                outputFile.set(config.outputFile)
//                versionPrefix.set(config.versionPrefix)
//            }
//        }
//    }

}
