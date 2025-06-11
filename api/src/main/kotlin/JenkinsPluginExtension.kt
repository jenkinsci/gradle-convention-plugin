import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

public interface JenkinsPluginExtension {

    // Jenkins core version to depend on
    public val jenkinsVersion: Property<String>

    // Plugin metadata
    public val pluginID: Property<String>
    public val pluginName: Property<String>
    public val description: Property<String>
    public val url: Property<String>
    public val issueTrackerUrl: Property<String>
    public val gitHubUrl: Property<String>

    // Developer & License metadata
    public val developers: ListProperty<Developer>
    public val licenses: ListProperty<License>

    // Compatibility Settings
    public val compatibleSinceVersion: Property<String>
    public val pluginFirstClassLoader: Property<String>
    public val maskClasses: ListProperty<String>

    // Java & Build config
    public val javaVersion: Property<String>
    public val jvmArgs: Property<String>

    // Publishing & Repositories
    public val repoUrl: Property<String>
    public val snapshotRepoUrl: Property<String>
    public val incrementalsRepoUrl: Property<String>
    public val configureRepositories: Property<Boolean>
    public val configurePublishing: Property<Boolean>

    // Plugin File Config
    public val fileExtension: Property<String>

    // Development Server
    public val workDir: Property<String>

    // Localizer
    public val localizerOutputDir: Property<String>

    // SCM tag & Versioning
    public val scmTag: Property<String>
    public val gitVersioning: Property<GitVersioning>
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
