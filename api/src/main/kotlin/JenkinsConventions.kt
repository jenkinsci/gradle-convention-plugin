import javax.management.Notification

public object JenkinsConventions {

    public const val CURRENT_JENKINS_LTS: String = "2.504.2"

    public const val MINIMUM_JENKINS_VERSION: String = "2.504.2"

    public const val JAVA_VERSION: String = "2.504.2"

    public const val COMMUNITY_GROUP_ID: String = "org.jenkins-ci.plugin"

    public const val COMMUNITY_SCM_PATTERN: String = "https://github.com/jenkinsci/{plugin-id}-plugin"

    public val DEFAULT_LICENSE: LicenseInfo = LicenseInfo(
        name = "Apache License, Version 2.0",
        url = "https://www.apache.org/licenses/LICENSE-2.0.txt",
        distribution = "repo"
    )

    private object Categories {
        const val BUILD = "build"
        const val SCM = "scm"
        const val NOTIFICATION = "notification"
        const val DEPLOYMENT = "deployment"
        const val SECURITY = "security"
        const val PIPELINE = "pipeline"
        const val TESTING = "testing"
        const val INTEGRATION = "integration"
        const val ADMINISTRATION = "administration"
        const val MISC = "misc"

        val ALL = setOf(
            BUILD, SCM, NOTIFICATION, DEPLOYMENT, SECURITY, PIPELINE, TESTING, INTEGRATION, ADMINISTRATION, MISC
        )
    }

    private object CommonDependencies {
        val WORKFLOW_STEP_API = DependencyInfo(
            "workflow-step-api",
            "700.v6e45cb_a_5a_a_21",
            "Pipeline Step API"
        )
        val WORKFLOW_CPS_API = DependencyInfo(
            "workflow-cps-plugin",
            "4106.v7a_8a_8176d450",
            "Pipeline CPS execution"
        )
        val CREDENTIALS = DependencyInfo(
            "credentials",
            "1415.v831096eb_5534",
            "Credentials API"
        )
        val STRUCTS = DependencyInfo(
            "structs",
            "350.v3b_30f09f2363",
            "Data Structure Support"
        )
        val PLAIN_CREDENTIALS = DependencyInfo(
            "plain-credentials",
            "195.vb_906e9073dee",
            "Plain text Credentials"
        )
        val SSH_CREDENTIALS = DependencyInfo(
            "ssh-credentials",
            "355.v9b_e5b_cde5003",
            "SSH credentials support"
        )
        val GIT = DependencyInfo(
            "git",
            "5.7.0",
            "Git SCM support"
        )
        val JENKINS_CORE = DependencyInfo(
            "jenkins-core",
            CURRENT_JENKINS_LTS,
            "Jenkins core"
        )
    }

    public object Naming {

        public fun projectNameToPluginId(projectName: String): String {
            return if (projectName.endsWith("-plugin")) {
                projectName.substring(0, projectName.length - 7)
            } else {
                projectName
            }
        }

        public fun pluginIdToDisplayName(pluginId: String): String {
            return pluginId.split("-").joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
        }

        public fun pluginIdToGitHubRepo(pluginId: String): String {
            return if (pluginId.endsWith("-")) {
                pluginId
            } else {
                "$pluginId-plugin"
            }
        }

    }

    public data class LicenseInfo(
        private val name: String,
        private val url: String,
        private val distribution: String = "repo"
    )

    public data class DependencyInfo(
        private val pluginId: String,
        private val version: String,
        private val description: String
    )

}
