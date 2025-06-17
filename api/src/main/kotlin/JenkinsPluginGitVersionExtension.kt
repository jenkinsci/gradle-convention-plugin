import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

public interface JenkinsPluginGitVersionExtension {

    public val versionFormat: Property<String>
    public val versionPrefix: Property<String>
    public val sanitize: Property<Boolean>
    public val abbrevLength: Property<Int>
    public val allowDirty: Property<Boolean>
    public val gitRoot: DirectoryProperty
    public val outputFile: RegularFileProperty

}
