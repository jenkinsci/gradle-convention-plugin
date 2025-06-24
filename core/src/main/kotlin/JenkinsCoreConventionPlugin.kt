import dependencies.JenkinsBillOfMaterials
import jpi.JpiPluginBridge
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.util.GradleVersion
import toolchain.JavaToolchainConfiguration

public class JenkinsCoreConventionPlugin : Plugin<Project> {

    public companion object {
        private const val MINIMUM_GRADLE_VERSION = "8.0"
        private val SUPPORTED_GRADLE_VERSION = GradleVersion.version(MINIMUM_GRADLE_VERSION)
    }

    private val jpiPluginBridge = JpiPluginBridge()

    override fun apply(project: Project) {

        // Validate Gradle Plugin
        validateGradleVersion(project)

        project.pluginManager.apply(JavaLibraryPlugin::class.java)

        JavaToolchainConfiguration.configureJavaToolchain(project)
        JavaToolchainConfiguration.validateJavaVersion(project)

        JenkinsBillOfMaterials.configureDefaultBOM(project)

        jpiPluginBridge.applyJpiPlugin(project)


        createJenkinsPluginExtension(project)

        JenkinsBillOfMaterials.configurePluginDependencies(project)

        configureConventions(project)

        project.logger.info("Applied Jenkins Core Convention Plugin to project: ${project.name}")
    }

    private fun validateGradleVersion(project: Project) {
        if (GradleVersion.current() < SUPPORTED_GRADLE_VERSION) {
            throw IllegalStateException("Requires Gradle $MINIMUM_GRADLE_VERSION+. Current: ${GradleVersion.current()}")
        }
    }

    private fun createJenkinsPluginExtension(project: Project) {
        val extension = project.extensions.create(
            JenkinsConventionExtension.EXTENSION_NAME, JenkinsPluginExtension::class.java, project
        )

        configureExtensionDefaults(extension, project)
    }

    private fun configureExtensionDefaults(extension: JenkinsPluginExtension, project: Project) = extension.apply {

        pluginId.convention(project.provider {
            JenkinsConventions.Naming.pluginIdToDisplayName(project.name)
        })

        humanReadableName.convention(project.provider {
            JenkinsConventions.Naming.pluginIdToDisplayName(project.name)
        })

        description.convention(project.provider {
            project.description ?: "A Jenkins plugin"
        })

        minimumJenkinsVersion.convention(JenkinsConventions.MINIMUM_JENKINS_VERSION)

        sandboxed.convention(false)
        usePluginFirstClassLoader.convention(false)
        requireEscapeByDefaultInJelly.convention(true)

        generateTests.convention(false)

        pipelineCompatible.convention(true)

        pluginCategories.convention(setOf(JenkinsConventions.Categories.MISC))

        project.afterEvaluate {
            if (!pluginLicenses.isPresent || pluginLicenses.get().isEmpty()) {
                apache2License()
            }
        }

    }

    private fun configureConventions(project: Project) {
        project.extensions.getByType(JavaPluginExtension::class.java).apply {
            modularity.inferModulePath.set(true)
            withSourcesJar()
            withJavadocJar()
        }
    }

    private fun derivePluginId(projectName: String): String =
        projectName.removeSuffix("-jenkins-plugin").removeSuffix("-plugin")


}
