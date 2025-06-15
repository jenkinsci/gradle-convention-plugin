import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

public interface JenkinsPluginDependency {

    public val pluginId: Property<String>
    public val version: Property<String>
    public val optional: Property<Boolean>
    public val features: SetProperty<String>
    public val reason: Property<String>

}
