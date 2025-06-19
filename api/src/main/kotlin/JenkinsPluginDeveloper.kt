import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.net.URI
import javax.inject.Inject

public abstract class JenkinsPluginDeveloper @Inject constructor() {

    public abstract val id: Property<String>
    public abstract val name: Property<String>
    public abstract val email: Property<String>
    public abstract val portfolioUrl: Property<URI>
    public abstract val organization: Property<String>
    public abstract val organizationUrl: Property<URI>
    public abstract val roles: SetProperty<String>
    public abstract val timezone: Property<String>

}
