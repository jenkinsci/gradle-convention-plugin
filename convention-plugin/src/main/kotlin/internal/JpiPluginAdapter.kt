package internal

import extensions.JenkinsPluginExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.jenkinsci.gradle.plugins.jpi.JpiExtension

public class JpiPluginAdapter(private val project: Project, private val pluginExtension: JenkinsPluginExtension) {

    private val jpiExtension: JpiExtension by lazy {
        project.extensions.getByType(JpiExtension::class.java)
    }

    public fun apply() {

        project.pluginManager.apply("java")
        project.pluginManager.apply(JpiExtension::class.java)

    }

    public fun configure() {
        bridgeExtensionProperties()
        configureJpiTask()
    }

    private fun bridgeExtensionProperties() {
        jpiExtension.apply {
            with(pluginExtension) {
                pluginId.set(pluginId.orNull)
                humanReadableName.set(humanReadableName.orNull)
                homePage.set(homePage.orNull)
                jenkinsVersion.set(jenkinsVersion.orNull)
                minimumJenkinsCoreVersion.set(minimumJenkinsCoreVersion.orNull)
                extension.set(extension.orNull)
                scmTag.set(scmTag.orNull)
                gitHub.set(githubUrl.orNull)
                generateTests.set(generateTests.orNull)
                generatedTestClassName.set(generatedTestClassName.orNull)
                sandboxed.set(sandboxed.orNull)
                usePluginFirstClassLoader.set(usePluginFirstClassLoader.orNull)
                maskedClassesFromCore.set(maskedClassesFromCore.orNull)

                pluginDevelopers.orNull?.forEach { dev ->
                    developers(Action { developers ->
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

                pluginLicenses.orNull?.forEach { lic ->
                    licenses(Action { licenses ->
                        licenses.license(Action { license ->
                            license.name.set(lic.name)
                            license.url.set(lic.url.toString())
                            license.distribution.set(lic.distribution)
                            license.comments.set(lic.comments)
                        })
                    })
                }

                incrementalsRepoUrl.set(incrementalsRepoUrl.orNull)
                testJvmArguments.set(testJvmArguments.orNull)
                requireEscapeByDefaultInJelly.set(requireEscapeByDefaultInJelly.orNull)
            }

        }

    }

    private fun configureJpiTask() {

    }


}
