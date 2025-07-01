package internal

import extensions.JenkinsPluginExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.newInstance
import org.jenkinsci.gradle.plugins.jpi.JpiExtension
import org.jenkinsci.gradle.plugins.jpi.JpiPlugin
import org.jenkinsci.gradle.plugins.jpi.core.PluginDeveloper
import org.jenkinsci.gradle.plugins.jpi.core.PluginLicense

public class JpiPluginAdapter(private val project: Project, private val pluginExtension: JenkinsPluginExtension) {

    private val jpiExtension: JpiExtension by lazy {
        project.extensions.getByType<JpiExtension>()
    }

    public fun apply() {

        project.pluginManager.apply("java-library")
        project.pluginManager.apply(JpiPlugin::class.java)

    }

    public fun configure() {
        bridgeExtensionProperties()
    }

    private fun bridgeExtensionProperties() {
        jpiExtension.apply {

            pluginId.set(pluginExtension.pluginId)
            humanReadableName.set(pluginExtension.humanReadableName)
            homePage.set(pluginExtension.homePage)
            jenkinsVersion.set(pluginExtension.jenkinsVersion)
            minimumJenkinsCoreVersion.set(pluginExtension.minimumJenkinsCoreVersion)
            extension.set(pluginExtension.extension)
            scmTag.set(pluginExtension.scmTag)
            gitHub.set(pluginExtension.githubUrl)
            generateTests.set(pluginExtension.generateTests)
            generatedTestClassName.set(pluginExtension.generatedTestClassName)
            sandboxed.set(pluginExtension.sandboxed)
            usePluginFirstClassLoader.set(pluginExtension.usePluginFirstClassLoader)
            maskedClassesFromCore.set(pluginExtension.maskedClassesFromCore)
            incrementalsRepoUrl.set(pluginExtension.incrementalsRepoUrl)
            testJvmArguments.set(pluginExtension.testJvmArguments)
            requireEscapeByDefaultInJelly.set(pluginExtension.requireEscapeByDefaultInJelly)

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
                }
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
                }
            )


        }

    }

}
