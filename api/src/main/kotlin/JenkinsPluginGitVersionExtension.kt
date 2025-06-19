import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

public abstract class JenkinsPluginGitVersionExtension @Inject constructor() {

    public abstract val versionFormat: Property<String>
    public abstract val versionPrefix: Property<String>
    public abstract val sanitize: Property<Boolean>
    public abstract val abbrevLength: Property<Int>
    public abstract val allowDirty: Property<Boolean>
    public abstract val gitRoot: DirectoryProperty
    public abstract val outputFile: RegularFileProperty

}
