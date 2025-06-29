import constants.PluginMetadata
import extensions.JenkinsPluginExtension
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

            val jpiAdapter = JpiPluginAdapter(project, pluginExtension)
            jpiAdapter.apply()

//            val managers = createManagers(project, pluginExtension)

            val manager = BomManager(project, pluginExtension)

            project.afterEvaluate {
                configureProject(project, pluginExtension, manager, jpiAdapter)
            }


        } catch (e: Exception) {
            throw GradleException("Failed to apply ${PluginMetadata.EXTENSION_NAME}: ${e.message}", e)
        }

    }

}

private fun createManagers(project: Project, pluginExtension: JenkinsPluginExtension) {

    // other managers would be added in future

    BomManager(project, pluginExtension)

}

private fun configureProject(
    project: Project,
    pluginExtension: JenkinsPluginExtension,
    manager: BomManager,
    jpiAdapter: JpiPluginAdapter,
) {
    jpiAdapter.configure()
    applyManagers(manager)
}

private fun applyManagers(manager: BomManager) {

    try {
        manager.configure()
    } catch (e: Exception) {
        throw GradleException("Failed to configure with ${manager.javaClass.name}: ${e.message}", e)
    }
}
