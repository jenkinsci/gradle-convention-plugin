import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import java.net.URI
import javax.inject.Inject

public abstract class JenkinsConventionExtension @Inject constructor(
    protected val project: Project,
    protected val objects: ObjectFactory = project.objects
) {

    public abstract val pluginId: Property<String>

    public abstract val humanReadableName: Property<String>

    public abstract val description: Property<String>

    public abstract val homePage: Property<URI>

    public abstract val sandboxed: Property<Boolean>

    public abstract val usePluginFirstClassLoader: Property<Boolean>

    public abstract val maskedClassesFromCore: SetProperty<String>

    public abstract val pluginDevelopers: ListProperty<JenkinsPluginDeveloper>

    public abstract val pluginLicenses: ListProperty<JenkinsPluginLicense>

    public abstract val pluginDependencies: ListProperty<JenkinsPluginDependency>

    public abstract val pluginCategories: SetProperty<String>

    public abstract val pluginLabels: SetProperty<String>

    public abstract val pipelineCompatible: Property<Boolean>

    public abstract val generateTests: Property<Boolean>

    public abstract val requireEscapeByDefaultInJelly: Property<Boolean>

    public abstract val gitVersion: Property<JenkinsPluginGitVersionExtension>

    public abstract val scmTag: Property<String>

    public abstract val minimumJenkinsVersion: Property<String>

    public abstract val extension: Property<String>

    public abstract val incrementalsRepoUrl: Property<URI>

    public abstract val testJvmArguments: ListProperty<String>

    public abstract val generatedTestClassName: Property<String>

    public abstract val workDir: DirectoryProperty

    public abstract val repoUrl: Property<URI>

    public abstract val snapshotRepoUrl: Property<URI>

    public abstract val configureRepositories: Property<Boolean>

    public abstract val configurePublishing: Property<Boolean>

    public open val gitHubUrl: Provider<URI> get() = computed.githubUrl
    public open val issueTrackerUrl: Provider<URI> get() = computed.computedIssueTrackerUrl
    public open val documentation: Provider<URI> get() = computed.computedDocumentationUrl

    // methods for Groovy/Java compatibility
    public open fun pluginDeveloper(action: Action<JenkinsPluginDeveloper>) {
        val developer = objects.newInstance(JenkinsPluginDeveloper::class.java)
        action.execute(developer)
        pluginDevelopers.add(developer)
    }

    public open fun pluginDeveloper(action: JenkinsPluginDeveloper.() -> Unit) {
        val developer = objects.newInstance(JenkinsPluginDeveloper::class.java)
        action(developer)
        pluginDevelopers.add(developer)
    }

    public open fun pluginLicense(action: Action<JenkinsPluginLicense>) {
        val license = objects.newInstance(JenkinsPluginLicense::class.java)
        action.execute(license)
        pluginLicenses.add(license)
    }

    public open fun pluginLicense(action: JenkinsPluginLicense.() -> Unit) {
        val license = objects.newInstance(JenkinsPluginLicense::class.java)
        action(license)
        pluginLicenses.add(license)
    }

    public open fun pluginDependency(action: Action<JenkinsPluginDependency>) {
        val dependency = objects.newInstance(JenkinsPluginDependency::class.java)
        action.execute(dependency)
        pluginDependencies.add(dependency)
    }

    public open fun pluginDependency(action: JenkinsPluginDependency.() -> Unit) {
        val dependency = objects.newInstance(JenkinsPluginDependency::class.java)
        action(dependency)
        pluginDependencies.add(dependency)
    }

    public open fun dependsOn(pluginId: String, action: Action<JenkinsPluginDependency>) {
        val dependency = objects.newInstance(JenkinsPluginDependency::class.java).apply {
            this.pluginId.set(pluginId)
        }
        action.execute(dependency)
        pluginDependencies.add(dependency)
    }

    public open fun dependsOn(pluginId: String, action: JenkinsPluginDependency.() -> Unit = {}) {
        val dependency = objects.newInstance(JenkinsPluginDependency::class.java).apply {
            this.pluginId.set(pluginId)
        }
        action(dependency)
        pluginDependencies.add(dependency)
    }

    public open fun dependsOn(pluginId: String, version: String = "latest", optional: Boolean = false) {
        dependsOn(pluginId) {
            this.version.set(version)
            this.optional.set(optional)
        }
    }

    public open fun gitVersion(action: Action<JenkinsPluginGitVersionExtension>) {
        val gitConfig = objects.newInstance(JenkinsPluginGitVersionExtension::class.java)
        action.execute(gitConfig)
        gitVersion.set(gitConfig)
    }

    public open fun gitVersion(action: JenkinsPluginGitVersionExtension.() -> Unit) {
        val gitConfig = objects.newInstance(JenkinsPluginGitVersionExtension::class.java)
        action(gitConfig)
        gitVersion.set(gitConfig)
    }

    public open val computed: JenkinsConventionComputed
        get() = JenkinsConventionComputed(this)

    public companion object {
        public const val EXTENSION_NAME: String = "jenkinsPlugin"
    }

}
