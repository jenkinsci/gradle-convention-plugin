import org.gradle.api.provider.Property
import java.net.URI
import javax.inject.Inject

public abstract class JenkinsPluginLicense @Inject constructor() {

    public abstract val name: Property<String>
    public abstract val url: Property<URI>
    public abstract val distribution: Property<String>
    public abstract val comments: Property<String>

}
