import org.gradle.api.provider.Property
import java.net.URI

public interface JenkinsPluginLicense {

    public val name: Property<String>
    public val url: Property<URI>
    public val distribution: Property<String>
    public val comments: Property<String>

}
