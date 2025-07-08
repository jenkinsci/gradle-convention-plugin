package internal

import extensions.JenkinsPluginExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.newInstance
import org.jenkinsci.gradle.plugins.jpi.JpiExtension
import org.jenkinsci.gradle.plugins.jpi.core.PluginDeveloper
import org.jenkinsci.gradle.plugins.jpi.core.PluginLicense

public class JpiPluginAdapter(
    private val project: Project,
    private val pluginExtension: JenkinsPluginExtension,
) {
    private val jpiExtension: JpiExtension by lazy {
        project.extensions.getByType<JpiExtension>()
    }

    init {
        project.pluginManager.apply("java")
    }

    public fun applyAndConfigure() {
        project.pluginManager.apply("org.jenkins-ci.jpi")

        project.afterEvaluate {
            bridgeExtensionProperties()
            project.tasks.findByName("generateLicenseInfo")?.enabled = false
        }
    }

    private fun bridgeExtensionProperties() =
        with(jpiExtension) {
            pluginId.convention(pluginExtension.pluginId)
            humanReadableName.convention(pluginExtension.humanReadableName)
            homePage.convention(pluginExtension.homePage)
            jenkinsVersion.convention(pluginExtension.jenkinsVersion)
            minimumJenkinsCoreVersion.convention(pluginExtension.minimumJenkinsCoreVersion)
            extension.convention(pluginExtension.extension)
            scmTag.convention(pluginExtension.scmTag)
            gitHub.convention(pluginExtension.githubUrl)
            generateTests.convention(pluginExtension.generateTests)
            generatedTestClassName.convention(pluginExtension.generatedTestClassName)
            sandboxed.convention(pluginExtension.sandboxed)
            usePluginFirstClassLoader.convention(pluginExtension.usePluginFirstClassLoader)
            maskedClassesFromCore.convention(pluginExtension.maskedClassesFromCore)
            incrementalsRepoUrl.convention(pluginExtension.incrementalsRepoUrl)
            testJvmArguments.convention(pluginExtension.testJvmArguments)
            requireEscapeByDefaultInJelly.convention(pluginExtension.requireEscapeByDefaultInJelly)

            pluginDevelopers.set(
                pluginExtension.pluginDevelopers.map { developers ->
                    developers.map { dev ->
                        project.objects.newInstance<PluginDeveloper>().apply {
                            id.set(dev.id)
                            name.set(dev.name)
                            email.set(dev.email)
                            url.set(dev.portfolioUrl.toString())
                            organization.set(dev.organization)
                            organizationUrl.set(dev.organizationUrl.toString())
                            roles.set(dev.roles)
                            timezone.set(dev.timezone)
                        }
                    }
                },
            )

            pluginLicenses.set(
                pluginExtension.pluginLicenses.map { licenses ->
                    licenses.map { lic ->
                        project.objects.newInstance<PluginLicense>().apply {
                            name.set(lic.name)
                            url.set(lic.url.toString())
                            distribution.set(lic.distribution)
                            comments.set(lic.comments)
                        }
                    }
                },
            )
        }
}
