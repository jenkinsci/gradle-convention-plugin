import org.gradle.api.provider.Property

public interface JenkinsPluginLicense {

    public val name: Property<String>
    public val url: Property<String>
    public val distribution: Property<String>
    public val comments: Property<String>

}
