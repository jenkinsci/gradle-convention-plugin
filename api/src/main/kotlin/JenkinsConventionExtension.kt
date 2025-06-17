import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.net.URI
import javax.inject.Inject

public abstract class JenkinsConventionExtension @Inject constructor(protected val project: Project) {

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

    public abstract val issueTrackerUrl: Property<URI>

    public abstract val gitVersion: Property<JenkinsPluginGitVersionExtension>

    public abstract val scmTag: Property<String>

    public abstract val minimumJenkinsVersion: Property<String>

    public abstract val extension: Property<String>

    public abstract val gitHub: Property<URI>

    public abstract val incrementalsRepoUrl: Property<URI>

    public abstract val testJvmArguments: ListProperty<String>

    public abstract val generatedTestClassName: Property<String>

    public abstract val workDir: DirectoryProperty

    public abstract val repoUrl: Property<URI>

    public abstract val snapshotRepoUrl: Property<URI>

    public abstract val configureRepositories: Property<Boolean>

    public abstract val configurePublishing: Property<Boolean>

    // methods for Groovy/Java compatibility
    public fun pluginDeveloper(action: Action<JenkinsPluginDeveloper>) {
        val developer = project.objects.newInstance(JenkinsPluginDeveloper::class.java)
        action.execute(developer)
        pluginDevelopers.add(developer)
    }

    public fun pluginLicense(action: Action<JenkinsPluginLicense>) {
        val license = project.objects.newInstance(JenkinsPluginLicense::class.java)
        action.execute(license)
        pluginLicenses.add(license)
    }

    public fun pluginDependency(action: Action<JenkinsPluginDependency>) {
        val dependency = project.objects.newInstance(JenkinsPluginDependency::class.java)
        action.execute(dependency)
        pluginDependencies.add(dependency)
    }

    public fun gitVersion(action: Action<JenkinsPluginGitVersionExtension>) {
        val gitConfig = project.objects.newInstance(JenkinsPluginGitVersionExtension::class.java)
        action.execute(gitConfig)
        gitVersion.set(gitConfig)
    }

    private val computed: JenkinsConventionComputed by lazy { JenkinsConventionComputed(this) }

}
