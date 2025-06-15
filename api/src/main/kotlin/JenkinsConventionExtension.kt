import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.net.URI
import javax.inject.Inject

public abstract class JenkinsConventionExtension @Inject constructor(protected val project: Project) {

    public abstract val pluginId: Property<String>

    public abstract val displayName: Property<String>

    public abstract val description: Property<String>

    public abstract val url: Property<URI>

    public abstract val sandboxed: Property<Boolean>

    public abstract val pluginFirstClassLoader: Property<Boolean>

    public abstract val maskClasses: SetProperty<String>

    public abstract val developers: ListProperty<JenkinsPluginDeveloper>

    public abstract val licenses: ListProperty<JenkinsPluginLicense>

    public abstract val dependencies: ListProperty<JenkinsPluginDependency>

    public abstract val categories: SetProperty<String>

    public abstract val labels: SetProperty<String>

    public abstract val pipelineCompatible: Property<Boolean>

    public abstract val generateTests: Property<Boolean>

    public abstract val requireEscapeByDefaultInJelly: Property<Boolean>

    public abstract val issueTrackerUrl: Property<URI>

    public abstract val scm: Property<JenkinsPluginScm>

    public abstract val minimumJenkinsVersion: Property<String>

    public abstract val fileExtension: Property<String>

    public fun developer(action: Action<JenkinsPluginDeveloper>) {
        val developer = project.objects.newInstance(JenkinsPluginDeveloper::class.java)
        action.execute(developer)
        developers.add(developer)
    }

    public fun license(action: Action<JenkinsPluginLicense>) {
        val license = project.objects.newInstance(JenkinsPluginLicense::class.java)
        action.execute(license)
        licenses.add(license)
    }

    public fun dependency(action: Action<JenkinsPluginDependency>) {
        val dependency = project.objects.newInstance(JenkinsPluginDependency::class.java)
        action.execute(dependency)
        dependencies.add(dependency)
    }

    public fun scm(action: Action<JenkinsPluginScm>) {
        val scmConfig = project.objects.newInstance(JenkinsPluginScm::class.java)
        action.execute(scmConfig)
        scm.set(scmConfig)
    }

    private val computed: JenkinsConventionComputed = JenkinsConventionComputed(this)

}
