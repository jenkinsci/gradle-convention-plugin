import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

public interface JenkinsPluginExtension {

    // Jenkins core version to depend on
    public val jenkinsVersion: Property<String>

    // Plugin metadata
    public val pluginId: Property<String>
    public val pluginName: Property<String>
    public val description: Property<String>
    public val url: Property<String>
    public val issueTrackerUrl: Property<String>
    public val githubUrl: Property<String>

    // Developer & License metadata
    public val developers: ListProperty<Developer>
    public val licenses: ListProperty<License>

    // Compatibility Settings
    public val compatibleSinceVersion: Property<String>
    public val pluginFirstClassLoader: Property<Boolean>
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

    // Additional Test Injection
    public val enableTestInjection: Property<Boolean>

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

public interface Developer {
    public val name: Property<String>
    public val id: Property<String>
    public val email: Property<String>
    public val organization: Property<String>
}

public interface License {
    public val name: Property<String>
    public val url: Property<String>
    public val distribution: Property<String>
    public val comments: Property<String>
}

public interface GitVersioning {
    public val allowDirty: Property<Boolean>
    public val versionFormat: Property<String>
    public val sanitize: Property<Boolean>
    public val abbrevLength: Property<Int>
    public val gitRoot: Property<String>
}
