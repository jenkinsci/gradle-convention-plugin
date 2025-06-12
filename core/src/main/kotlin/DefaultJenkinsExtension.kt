import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

public class DefaultJenkinsExtension @Inject constructor(
    objects: ObjectFactory,
    private val project: Project,
) : JenkinsPluginExtension {
    override val jenkinsVersion: Property<String> =
        objects.property(String::class.java)
            .convention(project.providers.gradleProperty("jenkins.version").orElse("2.513"))

    override val pluginId: Property<String> =
        objects.property(String::class.java)
            .convention(project.providers.gradleProperty("jenkins.pluginId").orElse(project.provider { project.name }))

    override val pluginName: Property<String> =
        objects.property(String::class.java).convention(
            project.providers.gradleProperty("jenkins.pluginName").orElse("Hello World plugin built with Gradle")
        )

    override val description: Property<String> =
        objects.property(String::class.java)
            .convention(project.providers.gradleProperty("jenkins.plugin.description").orElse("Hello World plugin"))

    override val url: Property<String> = objects.property(String::class.java)
        .convention(
            project.providers.gradleProperty("jenkins.plugin.url")
                .orElse(project.provider { "https://wiki.jenkins-ci.org/display/JENKINS/${project.name}" })
        )

    override val issueTrackerUrl: Property<String> =
        objects.property(String::class.java).convention(
            project.providers.gradleProperty("jenkins.plugin.issueTrackerUrl")
                .orElse("https://issues.jenkins.io/browse/JENKINS")
        )

    override val githubUrl: Property<String> =
        objects.property(String::class.java)
            .convention(
                project.providers.gradleProperty("jenkins.plugin.githubUrl")
                    .orElse(project.provider { "https://github.com/jenkinsci/${project.name}" })
            )

    override val developers: ListProperty<Developer> =
        objects.listProperty(Developer::class.java)
            .convention(listOf(objects.newInstance(DefaultDeveloper::class.java, project)))

    override val licenses: ListProperty<License> =
        objects.listProperty(License::class.java)
            .convention(listOf(objects.newInstance(DefaultLicense::class.java, project)))

    override val compatibleSinceVersion: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.compatibleSinceVersion").orElse("2.513"))

    override val pluginFirstClassLoader: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.pluginFirstClassLoader").map { it.toBoolean() }
            .orElse(true))

    override val maskClasses: ListProperty<String> = objects.listProperty(String::class.java)

    override val javaVersion: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.javaVersion").orElse("17"))

    override val jvmArgs: Property<String> =
        objects.property(String::class.java).convention(project.providers.gradleProperty("jenkins.jvmArgs").orElse(""))

    override val repoUrl: Property<String> =
        objects.property(String::class.java).convention(
            project.providers.gradleProperty("jenkins.plugin.repoUrl").orElse("https://repo.jenkins-ci.org/releases/")
        )

    override val snapshotRepoUrl: Property<String> =
        objects.property(String::class.java).convention(
            project.providers.gradleProperty("jenkins.plugin.snapshotRepoUrl")
                .orElse("https://repo.jenkins-ci.org/snapshots/")
        )

    override val incrementalsRepoUrl: Property<String> =
        objects.property(String::class.java).convention(
            project.providers.gradleProperty("jenkins.plugin.incrementalsRepoUrl")
                .orElse("https://repo.jenkins-ci.org/incrementals/")
        )

    override val configureRepositories: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.configureRepositories").map { it.toBoolean() }
            .orElse(false))

    override val configurePublishing: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.configurePublishing").map { it.toBoolean() }
            .orElse(false))

    override val enableTestInjection: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.enableTestInject").map { it.toBoolean() }
            .orElse(false))

    override val fileExtension: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.fileExtension").orElse("hpi"))

    override val workDir: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.workDir").orElse("/tmp/jenkins"))

    override val localizerOutputDir: Property<String> =
        objects.property(String::class.java).convention(
            project.providers.gradleProperty("jenkins.plugin.localizerOutputDir")
                .orElse(project.layout.buildDirectory.map { it.dir("generated-src/localizer").asFile.absolutePath })
        )

    override val scmTag: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.scmTag").orElse("HEAD"))

    override val gitVersioning: Property<GitVersioning> =
        objects.property(GitVersioning::class.java)
            .convention(objects.newInstance(DefaultGitVersioning::class.java, project))
}

public open class DefaultDeveloper @Inject constructor(
    objects: ObjectFactory,
    project: Project
) : Developer {
    override val name: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.developer.name").orElse("Developer Name"))
    override val id: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.developer.id").orElse("Developer ID"))
    override val email: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.developer.email").orElse("Developer Email"))
    override val organization: Property<String> = objects.property(String::class.java)
        .convention(
            project.providers.gradleProperty("jenkins.plugin.developer.organization").orElse("Developer Organization")
        )
}

public open class DefaultLicense @Inject constructor(
    objects: ObjectFactory,
    project: Project
) : License {
    override val name: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.license.name").orElse("MIT License"))
    override val url: Property<String> = objects.property(String::class.java).convention(
        project.providers.gradleProperty("jenkins.plugin.license.url").orElse("https://opensource.org/licenses/MIT")
    )
    override val distribution: Property<String> = objects.property(String::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.license.distribution").orElse("repo"))

    override val comments: Property<String> = objects.property(String::class.java)
        .convention(
            project.providers.gradleProperty("jenkins.plugin.license.comments")
                .orElse("Allows reuse within proprietary software provided all copies include the license.")
        )
}

public open class DefaultGitVersioning @Inject constructor(
    objects: ObjectFactory,
    project: Project
) : GitVersioning {

    override val allowDirty: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.gitVersioning.allowDirty").map { it.toBoolean() }
            .orElse(true))
    override val versionFormat: Property<String> = objects.property(String::class.java)
        .convention(
            project.providers.gradleProperty("jenkins.plugin.gitVersioning.versionFormat").orElse("v\${commit.short}")
        )
    override val sanitize: Property<Boolean> = objects.property(Boolean::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.gitVersioning.sanitize").map { it.toBoolean() }
            .orElse(true))
    override val abbrevLength: Property<Int> = objects.property(Int::class.java)
        .convention(project.providers.gradleProperty("jenkins.plugin.gitVersioning.abbrevLength").map { it.toInt() }
            .orElse(10))
    override val gitRoot: Property<String> = objects.property(String::class.java)
        .convention(
            project.providers.gradleProperty("jenkins.plugin.gitVersioning.gitRoot")
                .orElse(project.rootDir.absolutePath)
        )

}