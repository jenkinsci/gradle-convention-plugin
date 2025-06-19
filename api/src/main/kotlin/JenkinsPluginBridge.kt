import org.gradle.api.Project

public interface JenkinsPluginBridge {

    public fun applyJpiPlugin(project: Project)
    public fun configureJpiPlugin(project: Project, convention: JenkinsConventionExtension)
    public fun validateConfiguration(project: Project, convention: JenkinsConventionExtension): List<String>
    public fun getJpiExtension(project: Project): Any?

    public fun isTestEnvironment(project: Project): Boolean {
        return project.hasProperty("testing") || System.getProperty("org.gradle.test.worker") != null || project.gradle.startParameter.taskNames.any {
            it.contains(
                "test"
            )
        }
    }

    public fun isValidJenkinsVersion(version: String): Boolean {
        return version.matches(Regex("^\\d+\\.\\d{3}\\.\\d+$"))
    }

    public companion object {
        public const val JPI2_PLUGIN_ID: String = "org.jenkins-ci.jpi2"
        public const val JPI_PLUGIN_ID: String = "org.jenkins-ci.jpi"
    }

}
