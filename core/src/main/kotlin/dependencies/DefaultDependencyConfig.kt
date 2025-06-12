package dependencies

import JenkinsDependencyConfig
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

public class DefaultDependencyConfig @Inject constructor(
    private val project: Project,
    private val objects: ObjectFactory
) : JenkinsDependencyConfig {

    override val jenkinsVersion: Property<String> = objects.property(String::class.java).convention("2.513")

    override val useJenkinsBom: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

    override val jenkinsBomVersion: Property<String> = objects.property(String::class.java).convention(jenkinsVersion)

    override val jenkinsPlugins: SetProperty<JenkinsDependencyConfig.PluginDependency> =
        objects.setProperty(JenkinsDependencyConfig.PluginDependency::class.java).convention(emptySet())

    override val useRecommendedTestDependencies: Property<Boolean> =
        objects.property(Boolean::class.java).convention(false)

    override val additionalTestDependencies: SetProperty<String> =
        objects.setProperty(String::class.java).convention(emptySet())

    override fun jenkinsPlugin(pluginId: String, version: String, optional: Boolean) {
        val dependency = JenkinsDependencyConfig.PluginDependency(
            pluginId,
            version,
            optional
        )
        jenkinsPlugins.add(dependency)
    }

    override fun jenkinsPlugin(
        pluginId: String,
        version: String,
        configuration: Action<JenkinsDependencyConfig.PluginDependencyConfiguration>
    ) {
        val config = objects.newInstance(JenkinsDependencyConfig.PluginDependencyConfiguration::class.java, objects)
        configuration.execute(config)

        val dependency = JenkinsDependencyConfig.PluginDependency(
            pluginId,
            version,
            optional = config.optional.orNull ?: false,
            jenkinsVersion = config.jenkinsVersion.orNull
        )

        jenkinsPlugins.add(dependency)
    }

    override fun useRecommendedTestDependencies() {
        useRecommendedTestDependencies.set(true)
    }

    override fun additionalTestDependencies(vararg dependencies: String) {
        additionalTestDependencies.addAll(dependencies.toList())
    }

    public abstract class DefaultPluginDependencyConfiguration @Inject constructor(objects: ObjectFactory) :
        JenkinsDependencyConfig.PluginDependencyConfiguration {
        override val optional: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
        override val jenkinsVersion: Property<String> = objects.property(String::class.java)
    }
}