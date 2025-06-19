public object JenkinsConventions {

    public const val CURRENT_JENKINS_LTS: String = "2.504.2"

    public const val MINIMUM_JENKINS_VERSION: String = "2.504.2"

    public const val CURRENT_JENKINS_BOM_LTS_VERSION: String = "2.504.x"

    public const val CURRENT_JENKINS_BOM_VERSION: String = "4924.v6b_eb_a_79a_d9d0"

    public const val JAVA_VERSION: String = "24"

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

    public object CommonDependencies {
        public val WORKFLOW_STEP_API: DependencyInfo = DependencyInfo(
            "workflow-step-api",
            "Pipeline Step API"
        )
        public val WORKFLOW_CPS_API: DependencyInfo = DependencyInfo(
            "workflow-cps",
            "Pipeline CPS execution"
        )
        public val CREDENTIALS: DependencyInfo = DependencyInfo(
            "credentials",
            "Credentials API"
        )
        public val STRUCTS: DependencyInfo = DependencyInfo(
            "structs",
            "Data Structure Support"
        )
        public val PLAIN_CREDENTIALS: DependencyInfo = DependencyInfo(
            "plain-credentials",
            "Plain text Credentials"
        )
        public val SSH_CREDENTIALS: DependencyInfo = DependencyInfo(
            "ssh-credentials",
            "SSH credentials support"
        )
        public val GIT: DependencyInfo = DependencyInfo(
            "git",
            "Git SCM support"
        )

        public val ALL_PLUGINS: Set<DependencyInfo> = setOf(
            WORKFLOW_STEP_API, WORKFLOW_CPS_API, CREDENTIALS, STRUCTS, PLAIN_CREDENTIALS, SSH_CREDENTIALS, GIT
        )
    }

    public object Naming {

        public fun projectNameToPluginId(projectName: String): String {
            return when {
                projectName.endsWith("-jenkins-plugin") -> projectName.substring(0, projectName.length - 15)
                projectName.endsWith("-plugin") -> projectName.substring(0, projectName.length - 7)
                else -> projectName
            }
        }

        public fun pluginIdToDisplayName(pluginId: String): String {
            return pluginId.split("-").joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
        }

        public fun pluginIdToGitHubRepo(pluginId: String): String {
            return if (pluginId.endsWith("-plugin")) {
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
        val pluginId: String,
        private val description: String
    )
}
