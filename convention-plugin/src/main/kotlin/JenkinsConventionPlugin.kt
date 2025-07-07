import constants.PluginMetadata
import extensions.BomExtension
import extensions.JenkinsPluginExtension
import extensions.QualityExtension
import internal.BomManager
import internal.JpiPluginAdapter
import internal.QualityManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import utils.GradleVersionUtils

public class JenkinsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        GradleVersionUtils.verifyGradleVersion(project)
        val libs: VersionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

        val pluginExtension =
            project.extensions.create<JenkinsPluginExtension>(
                PluginMetadata.EXTENSION_NAME,
                project,
            )
        val bomExtension =
            project.extensions.create(
                "bom",
                BomExtension::class.java,
                project.objects,
                libs,
            )

        val qualityExtension = project.extensions.create("quality", QualityExtension::class.java, project, libs)

        val jpiAdapter = JpiPluginAdapter(project, pluginExtension)
        val bomManager = BomManager(project, bomExtension)
        val qualityManager = QualityManager(project, qualityExtension)

        bomManager.configure()
        qualityManager.apply()

        project.afterEvaluate {
            jpiAdapter.applyAndConfigure()
        }
    }
}
