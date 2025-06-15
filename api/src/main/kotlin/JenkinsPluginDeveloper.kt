import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import java.net.URI

public interface JenkinsPluginDeveloper {

    public val id: Property<String>
    public val name: Property<String>
    public val email: Property<String>
    public val portfolioUrl: Property<URI>
    public val organization: Property<String>
    public val organizationUrl: Property<URI>
    public val roles: SetProperty<String>
    public val timezone: Property<String>

}
