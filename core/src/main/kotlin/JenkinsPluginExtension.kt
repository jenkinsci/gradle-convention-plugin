import org.gradle.api.Project
import javax.inject.Inject

public abstract class JenkinsPluginExtension @Inject constructor(project: Project) :
    JenkinsConventionExtension(project) {

    public fun developer(action: JenkinsPluginDeveloper.() -> Unit) {
        val developer = project.objects.newInstance(JenkinsPluginDeveloper::class.java, project)
        action(developer)
        developers.add(developer)
    }

    public fun license(action: JenkinsPluginLicense.() -> Unit) {
        val license = project.objects.newInstance(JenkinsPluginLicense::class.java, project)
        action(license)
        licenses.add(license)
    }

    public fun dependsOn(pluginId: String, action: JenkinsPluginDependency.() -> Unit = {}) {
        val dependency = project.objects.newInstance(JenkinsPluginDependency::class.java, project).apply {
            this.pluginId.set(pluginId)
        }
        action(dependency)
        dependencies.add(dependency)
    }

    public fun optionallyDependsOn(pluginId: String, action: JenkinsPluginDependency.() -> Unit = {}) {
        dependsOn(pluginId) {
            optional.set(true)
            action()
        }
    }

    public fun scm(action: JenkinsPluginScm.() -> Unit) {
        val scmInfo = project.objects.newInstance(JenkinsPluginScm::class.java, project)
        action(scmInfo)
        scm.set(scmInfo)
    }

    public fun apache2License() {
        license {
            name.set("Apache License Version 2.0")
            url.set(java.net.URI("https://www.apache.org/licenses/LICENSE-2.0.txt"))
            distribution.set("repo")
        }
    }

    public fun mitLicense() {
        license {
            name.set("MIT License")
            url.set(java.net.URI("https://opensource.org/license/mit"))
            distribution.set("repo")
        }
    }

}
