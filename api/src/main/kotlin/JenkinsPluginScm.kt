import org.gradle.api.provider.Property
import java.net.URI

public interface JenkinsPluginScm {

    public val connection: Property<String>
    public val developerConnection: Property<String>
    public val url: Property<URI>
    public val tag: Property<String>

}
