package internal

import extensions.JenkinsPluginExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

public class BomManager(private val project: Project, private val pluginExtension: JenkinsPluginExtension) {

    public fun configure() {
        configureCoreBoM()
        configurePluginBoM()
    }

    private fun configureCoreBoM() {

        val bomVersion = "4969.v6ffa_18d90c9f"

        project.dependencies {
            add("implementation", platform("io.jenkins.tools.bom:bom-${bomVersion}:${bomVersion}"))
        }

    }

    private fun configurePluginBoM() {

        val bomVersion = "4969.v6ffa_18d90c9f"

        project.dependencies {
            add("implementation", platform("io.jenkins.plugins:plugin-bom:${bomVersion}"))
        }

    }
}
