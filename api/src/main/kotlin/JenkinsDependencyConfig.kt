import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

public interface JenkinsDependencyConfig {

    public val jenkinsVersion: Property<String>
    public val useJenkinsBom: Property<Boolean>
    public val jenkinsBomVersion: Property<String>
    public val jenkinsPlugins: SetProperty<PluginDependency>
    public val useRecommendedTestDependencies: Property<Boolean>
    public val additionalTestDependencies: SetProperty<String>
    public fun jenkinsPlugin(pluginId: String, version: String, optional: Boolean = false)
    public fun jenkinsPlugin(pluginId: String, version: String, configuration: Action<PluginDependencyConfiguration>)
    public fun useRecommendedTestDependencies()
    public fun additionalTestDependencies(vararg dependencies: String)

    public data class PluginDependency(
        val pluginId: String,
        val version: String,
        val optional: Boolean = false,
        val jenkinsVersion: String? = null
    )

    public interface PluginDependencyConfiguration {
        public val optional: Property<Boolean>
        public val jenkinsVersion: Property<String>
    }
}