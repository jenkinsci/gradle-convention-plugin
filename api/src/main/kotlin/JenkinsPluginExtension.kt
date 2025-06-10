public interface JenkinsPluginExtension {

    // Jenkins core version to depend on
    public var jenkinsVersion: String?

    // Plugin metadata
    public var pluginID: String
    public var pluginName: String
    public var description: String?
    public var url: String?
    public var issueTrackerUrl: String?
    public var gitHubUrl: String?

    // Developer & License metadata
    public var developers: List<Developer>
    public var licenses: List<License>

    // Compatibility Settings
    public var compatibleSinceVersion: String
    public var pluginFirstClassLoader: String
    public var maskClasses: List<String>

    // Java & Build config
    public var javaVersion: String
    public var jvmArgs: String?

    // Publishing & Repositories
    public var repoUrl: String?
    public var snapshotRepoUrl: String?
    public var incrementalsRepoUrl: String?
    public var configureRepositories: Boolean
    public var configurePublishing: Boolean

    // Plugin File Config
    public var fileExtension: String?

    // Development Server
    public var workDir: String?

    // Localizer
    public var localizerOutputDir: String?

    // SCM tag & Versioning
    public var scmTag: String?
    public var gitVersioning: GitVersioning?
}

public data class Developer(
    val name: String,
    val id: String,
    val email: String,
    val organization: String?
)

public data class License(
    val name: String,
    val url: String,
    val distribution: String? = null,
    val comments: String? = null
)

public data class GitVersioning(
    val allowDirty: Boolean?,
    val versionFormat: String?,
    val sanitize: Boolean?,
    val abbrevLength: Int?,
    val gitRoot: String?
)
