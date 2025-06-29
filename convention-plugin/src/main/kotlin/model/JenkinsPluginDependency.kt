package model

import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import javax.inject.Inject

public abstract class JenkinsPluginDependency @Inject constructor() {

    public abstract val pluginId: Property<String>
    public abstract val version: Property<String>
    public abstract val optional: Property<Boolean>
    public abstract val features: SetProperty<String>
    public abstract val reason: Property<String>

    init {
        optional.convention(false)
        features.convention(emptySet())
    }

}
