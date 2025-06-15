import org.gradle.api.Project

public interface JenkinsPluginBridge {

    public fun applyJpiPlugin(project: Project)
    public fun configureJpiPlugin(project: Project, convention: JenkinsConventionExtension)
    public fun validateConfiguration(project: Project, convention: JenkinsConventionExtension): List<String>
    public fun getJpiExtension(project: Project): Any?

    public companion object {
        public const val JPI2_PLUGIN_ID: String = "org.jenkins-ci.jpi2"
        public const val JPI_PLUGIN_ID: String = "org.jenkins-ci.jpi"
    }

}
