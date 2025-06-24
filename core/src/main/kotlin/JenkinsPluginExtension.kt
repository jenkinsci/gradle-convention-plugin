import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.net.URI
import javax.inject.Inject

public abstract class JenkinsPluginExtension @Inject constructor(project: Project) :
    JenkinsConventionExtension(project) {

    override val computed: JenkinsConventionComputed = JenkinsConventionComputed(this)

    public
    val artifactId: Provider<String> get() = computed.artifactId

    public
    val groupId: Provider<String> get() = computed.groupId

    public
    val isPipelinePlugin: Provider<Boolean> get() = computed.isPipelinePlugin

    override fun addPluginDeveloper(action: Action<JenkinsPluginDeveloper>) {
        val developer = project.objects.newInstance(JenkinsPluginDeveloper::class.java)
        action.execute(developer)
        pluginDevelopers.add(developer)
    }

    public override fun addPluginDeveloper(action: JenkinsPluginDeveloper.() -> Unit) {
        val developer = project.objects.newInstance(JenkinsPluginDeveloper::class.java, project)
        action(developer)
        pluginDevelopers.add(developer)
    }

    override fun addPluginLicense(action: Action<JenkinsPluginLicense>) {
        val license = project.objects.newInstance(JenkinsPluginLicense::class.java, project)
        action.execute(license)
        pluginLicenses.add(license)
    }

    public override fun addPluginLicense(action: JenkinsPluginLicense.() -> Unit) {
        val license = project.objects.newInstance(JenkinsPluginLicense::class.java, project)
        action(license)
        pluginLicenses.add(license)
    }

    override fun dependsOn(pluginId: String, action: Action<JenkinsPluginDependency>) {
        val dependency = project.objects.newInstance(JenkinsPluginDependency::class.java).apply {
            this.pluginId.set(pluginId)
        }
        action.execute(dependency)
        pluginDependencies.add(dependency)
    }

    override fun dependsOn(pluginId: String, version: String, optional: Boolean) {
        dependsOn(pluginId) {
            this.version.set(version)
            this.optional.set(false)
        }
    }

    public override fun dependsOn(pluginId: String, action: JenkinsPluginDependency.() -> Unit) {
        val dependency = project.objects.newInstance(JenkinsPluginDependency::class.java, project).apply {
            this.pluginId.set(pluginId)
        }
        action(dependency)
        pluginDependencies.add(dependency)
    }

    public override fun gitVersion(action: JenkinsPluginGitVersionExtension.() -> Unit) {
        val gitVersionInfo = project.objects.newInstance(JenkinsPluginGitVersionExtension::class.java, project)
        action(gitVersionInfo)
        gitVersion.set(gitVersionInfo)
    }

    public fun apache2License() {
        addPluginLicense {
            name.set("Apache License Version 2.0")
            url.set(java.net.URI("https://www.apache.org/licenses/LICENSE-2.0.txt"))
            distribution.set("repo")
        }
    }

    public fun mitLicense() {
        addPluginLicense {
            name.set("MIT License")
            url.set(java.net.URI("https://opensource.org/license/mit"))
            distribution.set("repo")
        }
    }

    public fun configureRepositories() {
        repoUrl.set(URI.create("https://repo.jenkins-ci.org/releases"))
        snapshotRepoUrl.set(URI.create("https://repo.jenkins-ci.org/snapshots"))
    }

    public fun configureRepositories(releaseUrl: URI, snapshotUrl: URI = releaseUrl) {
        repoUrl.set(releaseUrl)
        snapshotRepoUrl.set(snapshotUrl)
    }

    public fun setWorkDirectory(directory: String) {
        workDir.set(project.layout.projectDirectory.dir(directory).asFile)
    }

    public fun getJpiExtension(): Any? = project.extensions.findByName("jenkinsPlugin")

    public override fun addPluginDependency(action: Action<JenkinsPluginDependency>) {
        val dependency = objects.newInstance(JenkinsPluginDependency::class.java)
        action.execute(dependency)
        pluginDependencies.add(dependency)
    }

    public override fun addPluginDependency(action: JenkinsPluginDependency.() -> Unit) {
        val dependency = objects.newInstance(JenkinsPluginDependency::class.java)
        action(dependency)
        pluginDependencies.add(dependency)
    }

}
