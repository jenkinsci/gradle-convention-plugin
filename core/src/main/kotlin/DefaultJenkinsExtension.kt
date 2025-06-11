import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

public abstract class DefaultJenkinsExtension @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project,
) : JenkinsPluginExtension {
    override val jenkinsVersion: Property<String> =
        objects.property(String::class.java).convention("2.513")
    override val pluginID: Property<String> =
        objects.property(String::class.java).convention(project.provider { project.name })
    override val pluginName: Property<String> =
        objects.property(String::class.java).convention("Hello World plugin built with Gradle")
    override val description: Property<String> =
        objects.property(String::class.java).convention("Hello World plugin")
    override val url: Property<String> = objects.property(String::class.java)
        .convention("https://wiki.jenkins-ci.org/display/JENKINS/${project.name}")
    override val issueTrackerUrl: Property<String> =
        objects.property(String::class.java).convention("https://issues.jenkins.io/browse/JENKINS")
    override val gitHubUrl: Property<String> =
        objects.property(String::class.java)
            .convention("https://github.com/jenkinsci/${project.name}")
    override val developers: ListProperty<Developer> =
        objects.listProperty(Developer::class.java).convention(listOf(objects.newInstance(Developer::class.java).apply {
            id.set("your-github-id")
            name.set("Your Name")
            email.set("you@example.com")
            organization.set("Your Organization's Name")
        }))
    override val licenses: ListProperty<License> =
        objects.listProperty(License::class.java).convention(listOf(objects.newInstance(License::class.java).apply {
            name.set("MIT License")
            url.set("https://opensource.org/licenses/MIT")
            distribution.set("A short and permissive license often used for open source projects.")
            comments.set("Allows reuse within proprietary software provided all copies include the license.")
        }))
    override val compatibleSinceVersion: Property<String> = objects.property(String::class.java).convention("2.513")
    override val pluginFirstClassLoader: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
    override val maskClasses: ListProperty<String> = objects.listProperty(String::class.java)
    override val javaVersion: Property<String> = objects.property(String::class.java).convention("17")
    override val jvmArgs: Property<String> = objects.property(String::class.java)
    override val repoUrl: Property<String> =
        objects.property(String::class.java).convention("https://repo.jenkins-ci.org/releases/")
    override val snapshotRepoUrl: Property<String> =
        objects.property(String::class.java).convention("https://repo.jenkins-ci.org/snapshots/")
    override val incrementalsRepoUrl: Property<String> =
        objects.property(String::class.java).convention("https://repo.jenkins-ci.org/incrementals/")
    override val configureRepositories: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val configurePublishing: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val disabledTestInjection: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    override val fileExtension: Property<String> = objects.property(String::class.java).convention("hpi")
    override val workDir: Property<String> = objects.property(String::class.java).convention("/tmp/jenkins")
    override val localizerOutputDir: Property<String> =
        objects.property(String::class.java).convention("${project.layout.buildDirectory}/generated-src/localizer")
    override val scmTag: Property<String> = objects.property(String::class.java).convention("HEAD")
    override val gitVersioning: Property<GitVersioning> =
        objects.property(GitVersioning::class.java).convention(objects.newInstance(GitVersioning::class.java).apply {
            allowDirty.set(true)
            versionFormat.set("v${'$'}{commit.short}")
            sanitize.set(true)
            abbrevLength.set(10)
            gitRoot.set("/some/external/git/repo")
        })
}