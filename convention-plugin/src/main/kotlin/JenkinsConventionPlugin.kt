package io.jenkins.gradle

import constants.PluginMetadata
import extensions.JenkinsPluginExtension
import extensions.PublishingExtension
import internal.BomManager
import internal.JpiPluginAdapter
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

public class JenkinsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        try {
            val pluginExtension = project.extensions.create<JenkinsPluginExtension>(
                PluginMetadata.EXTENSION_NAME,
                project
            )
            val publishingExtension = project.extensions.create<PublishingExtension>(
                "jenkinsPublishing",
                PublishingExtension::class.java,
                project.objects
            )

            val jpiAdapter = JpiPluginAdapter(project, pluginExtension)
            val bomManager = BomManager(project, publishingExtension)

            jpiAdapter.apply()

            project.afterEvaluate {
                configureProject(project, jpiAdapter, bomManager)
            }

        } catch (e: Exception) {
            throw GradleException("Failed to apply ${PluginMetadata.EXTENSION_NAME}: ${e.message}", e)
        }

    }

}

private fun configureProject(
    project: Project,
    jpiPluginAdapter: JpiPluginAdapter,
    bomManager: BomManager,
) {

    jpiPluginAdapter.configure()
    bomManager.configure()

}
