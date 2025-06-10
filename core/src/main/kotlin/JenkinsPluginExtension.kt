//import org.gradle.api.GradleException
//import org.gradle.api.model.ObjectFactory
//import org.gradle.api.provider.ListProperty
//import org.gradle.api.provider.Property
//import java.net.URI
//import javax.inject.Inject
//
//open class JenkinsPluginExtension @Inject constructor(objects: ObjectFactory) {
//
//    // Jenkins core version to depend on
//    val jenkinsVersion: Property<String> = objects.property(String::class.java).convention("")
//
//    // Plugin coordinates
//    val pluginID: Property<String> = objects.property(String::class.java)
//    val pluginName: Property<String> = objects.property(String::class.java)
//    val description: Property<String> = objects.property(String::class.java).convention("A Jenkins Plugin")
//    val url: Property<String> = objects.property(String::class.java)
//    val issueTrackerUrl: Property<String> = objects.property(String::class.java)
//    val gitHubUrl: Property<String> = objects.property(String::class.java)
//
//    // SCM tag
//    val scmTag: Property<String> = objects.property(String::class.java)
//
//    // Developer & License metadata
//    val developers: ListProperty<Developer> = objects.listProperty(Developer::class.java)
//    val licenses: ListProperty<License> = objects.listProperty(License::class.java)
//
//    // Compatibility Settings
//    val compatibleSinceVersion: Property<String> = objects.property(String::class.java)
//    val pluginFirstClassLoader: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
//    val maskClasses: ListProperty<String> = objects.listProperty(String::class.java)
//
//    // Java target version (17 or later)
//    val javaVersion: Property<String> = objects.property(String::class.java).convention("17")
//
//    // JVM args for all plugin tasks
//    val jvmArgs: ListProperty<String> = objects.listProperty(String::class.java)
//
//    // Publishing & repositories
//    val repoUrl: Property<String> =
//        objects.property(String::class.java).convention("https://repo.jenkins-ci.org/releases")
//    val snapshotRepoUrl: Property<String> =
//        objects.property(String::class.java).convention("https://repo.jenkins-ci.org/snapshots")
//    val incrementalsRepoUrl: Property<String> =
//        objects.property(String::class.java).convention("https://repo.jenkins-ci.org/incrementals")
//    val configureRepositories: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
//    val configurePublishing: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
//
//    // Plugin file configuration
//    val fileExtension: Property<String> = objects.property(String::class.java).convention("hpi")
//
//    // Development Server
//    val workDir: Property<String> = objects.property(String::class.java).convention("/tmp/jenkins")
//
//    // Localizer
//    val localizerOutputDir: Property<String> =
//        objects.property(String::class.java).convention("build/generated-src/localizer")
//
//    // Version Generation (Git)
//    val gitVersioning: GitVersioning = objects.newInstance(GitVersioning::class.java, objects)
//
//    fun validate() {
//
//        fun requireNonBlank(prop: Property<*>, name: String) {
//            if (prop.orNull?.toString().isNullOrBlank()) {
//                throw GradleException("$name must be set & non-blank")
//            }
//        }
//
//        // Plugin ID
//        requireNonBlank(pluginID, "pluginID")
//        require(pluginID.get().matches(Regex("^[a-z][a-z0-9-_]+$"))) {
//            "plugin id must start with a letter & contain only lowercase letters, digits, '-', '_' or '.'"
//        }
//
//        // Plugin name & description
//        requireNonBlank(pluginName, "pluginName")
//        requireNonBlank(description, "description")
//
//        // URLs
//        requireNonBlank(url, "url")
//        validateHttpsUrl(url.get(), "url")
//        issueTrackerUrl.orNull?.let {
//            validateHttpsUrl(it, "issueTrackerUrl")
//        }
//        gitHubUrl.orNull?.let {
//            validateHttpsUrl(it, "gitHubUrl")
//            require(URI.create(it).host.contains("github.com")) { "gitHubUrl must point to github.com" }
//        }
//
//        // Jenkins version
//        requireNonBlank(jenkinsVersion, "jenkinsVersion")
//        require(jenkinsVersion.get().matches(Regex("^\\d+\\.\\d+\\.\\d+(?:[-.\\w]*)?$"))) {
//            "jenkinsVersion must be a valid semver, e.g., 2.462.3"
//        }
//
//        // SCM tag
//        scmTag.orNull?.let {
//            require(it.matches(Regex("^v?\\\\d+\\\\.\\\\d+\\\\.\\\\d+(?:[-.\\\\w]*)?$"))) {
//                "scmTag must be a valid version tag, e.g., v1.0.0"
//            }
//        }
//
//        // Developers
//        require(developers.get().isNotEmpty()) { "At least one developer must be configured" }
//        developers.get().forEach { dev ->
//            require(dev.id.matches(Regex("^[a-z0-9]+$"))) { "Developer id '${dev.id}' is invalid" }
//            require(dev.email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$"))) { "Developer email '${dev.email}' is invalid" }
//        }
//
//        // Licenses
//        require(licenses.get().isNotEmpty()) { "At least one license must be configured" }
//        licenses.get().forEach { lic ->
//            require(lic.name.isNotBlank()) { "License name must be set & non-blank" }
//            validateHttpsUrl(lic.url, "License URL")
//        }
//
//        // Java Version
//        require(javaVersion.get().toInt() >= 17) { "javaVersion must be 17 or later" }
//
//        // Repositories
//        validateHttpsUrl(repoUrl.get(), "repoUrl")
//        validateHttpsUrl(snapshotRepoUrl.get(), "snapshotRepoUrl")
//        validateHttpsUrl(incrementalsRepoUrl.get(), "incrementalsRepoUrl")
//
//        // File Extension
//        require(fileExtension.get() in listOf("jpi", "hpi")) { "fileExtension must be 'jpi' or 'hpi'" }
//
//        // Git Versioning
//        gitVersioning.validate()
//    }
//
//}
//
//private fun validateHttpsUrl(url: String, name: String) {
//    try {
//        val uri = URI.create(url)
//        require(uri.scheme == "https") { "$name must use HTTPS" }
//    } catch (_: IllegalArgumentException) {
//        throw GradleException("$name is not a valid URL: $url")
//    }
//}
//
//data class Developer(
//    val name: String,
//    val email: String,
//    val id: String
//)
//
//data class License(
//    val name: String,
//    val url: String,
//    val distribution: String? = null,
//    val comments: String? = null
//)
//
//abstract class GitVersioning @Inject constructor(objects: ObjectFactory) {
//    val allowDirty: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
//    val versionFormat: Property<String> = objects.property(String::class.java).convention("%d.%s")
//    val sanitize: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
//    val abbrevLength: Property<Int> = objects.property(Int::class.java).convention(12)
//    val gitRoot: Property<String> = objects.property(String::class.java)
//
//    fun validate() {
//        require(abbrevLength.get() in 6..40) { "abbrevLength must between 6 & 40" }
//    }
//
//}