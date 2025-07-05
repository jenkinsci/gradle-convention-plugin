import constants.PluginMetadata
import extensions.BomExtension
import extensions.JenkinsPluginExtension
import extensions.QualityExtension
import internal.BomManager
import internal.JpiPluginAdapter
import internal.QualityManager
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType

public class JenkinsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val libs: VersionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

        try {
            val pluginExtension =
                project.extensions.create<JenkinsPluginExtension>(
                    PluginMetadata.EXTENSION_NAME,
                    project,
                    libs,
                )
            val bomExtension =
                project.extensions.create(
                    "bom",
                    BomExtension::class.java,
                    project.objects,
                    libs,
                )

            val qualityExtension = project.extensions.create("quality", QualityExtension::class.java, project)

            val jpiAdapter = JpiPluginAdapter(project, pluginExtension)
            val bomManager = BomManager(project, bomExtension)
            val qualityManager = QualityManager(project, qualityExtension)

            jpiAdapter.apply()

            project.afterEvaluate {
                configureProject(project, jpiAdapter, bomManager, qualityManager)
            }
        } catch (e: IllegalStateException) {
            throw GradleException("Failed to apply ${PluginMetadata.EXTENSION_NAME}: ${e.message}", e)
        }
    }
}

private fun configureProject(
    project: Project,
    jpiPluginAdapter: JpiPluginAdapter,
    bomManager: BomManager,
    qualityManager: QualityManager,
) {
    jpiPluginAdapter.configure()
    bomManager.configure()
    qualityManager.apply()
}
