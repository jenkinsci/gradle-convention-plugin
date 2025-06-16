package dependencies

import JenkinsConventions
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies

public object JenkinsBillOfMaterials {

    private const val JENKINS_BOM_GROUP = "io.jenkins.tools.bom"

    public fun configureDefaultBOM(project: Project) {
        val ltsLine = JenkinsConventions.CURRENT_JENKINS_BOM_LTS_LINE
        val bomVersion = JenkinsConventions.CURRENT_JENKINS_BOM_VERSION

        configureSpecificBom(project, ltsLine, bomVersion)
    }

    public fun configureSpecificBom(project: Project, ltsLine: String, bomVersion: String) {
        val bomCoordinates = "$JENKINS_BOM_GROUP:bom-$ltsLine:$bomVersion"
        val enforcedBom = project.dependencies.enforcedPlatform(bomCoordinates)

        project.dependencies {
            add("implementation", enforcedBom)
            add("testImplementation", enforcedBom)
        }

        configureCoreAndTestDependencies(project.dependencies)
        configureCommonPluginDependencies(project.dependencies)
    }

    private fun configureCoreAndTestDependencies(dependencies: DependencyHandler) {
        dependencies.add("compileOnly", "org.jenkins-ci.main:jenkins-core")
        dependencies.add("compileOnly", "jakarta.servlet:jakarta.servlet-api")
        dependencies.add("annotationProcessor", "org.jenkins-ci.main:jenkins-core")

        dependencies.add("testImplementation", "org.jenkins-ci.main:jenkins-core")
    }

    private fun configureCommonPluginDependencies(dependencies: DependencyHandler) {
        JenkinsConventions.CommonDependencies.ALL_PLUGINS.forEach { dep ->
            dependencies.add("implementation", "org.jenkins-ci.main:${dep.pluginId}")
        }
    }
}
