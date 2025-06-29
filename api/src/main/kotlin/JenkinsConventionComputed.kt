import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.internal.sharedruntime.codegen.generateKotlinDslApiExtensionsSourceTo
import java.net.URI

public class JenkinsConventionComputed(private val extension: JenkinsConventionExtension) {

//    public val githubUrl: Provider<URI> = extension.gitHubUrl.map { url ->
//        val connection = url.toString()
//
//        val repoPattern = """github\.com[:/]([^/]+/[^/.]+)""".toRegex()
//        val match = repoPattern.find(connection)
//
//        if (match != null) {
//            URI.create("https://github.com/${match.groupValues[1]}")
//        } else {
//            URI.create("https://github.com/jenkinsci/unknown")
//        }
//    }

    public val computedIssueTrackerUrl: Provider<URI> = githubUrl.map { github ->
        URI.create("${github}/issues")
    }

    public val computedDocumentationUrl: Provider<URI> = githubUrl.map { github ->
        URI.create("${github}/blob/main/README.md")
    }

    public val artifactId: Provider<String> = extension.pluginId

    public val groupId: Provider<String> = extension.pluginId.map {
        "org.jenkins-ci.plugins"
    }

    public val archiveBaseName: Provider<String> = extension.pluginId

    public val isPipelinePlugin: Provider<Boolean> =
        extension.pluginId.zip(extension.pluginCategories) { id, categories ->
            val dependencies = extension.pluginDependencies.get()
            id.contains("pipeline") || id.contains("workflow") || categories.any { it.contains("pipeline") } || dependencies.any {
                it.pluginId.get().contains("workflow")
            }
        }

    public val isBuildPlugin: Provider<Boolean> =
        extension.pluginId.zip(extension.pluginCategories) { id, categories ->
            id.contains("build") || id.contains("gradle") || id.contains("maven") || id.contains("ant") || categories.any { it == "build" }
        }

    public val isScmPlugin: Provider<Boolean> = extension.pluginId.zip(extension.pluginCategories) { id, categories ->
        id.contains("git") || id.contains("svn") || id.contains("mercurial") || id.contains("scm") || id.contains("vcs") || id.contains(
            "github"
        ) || id.contains("bitbucket") || id.contains("gitlab") || id.contains("perforce") || categories.any { it == "scm" }
    }

    public val pluginFileName: Provider<String> = extension.pluginId.map {
        "$it.hpi"
    }

    public val qualifiedDisplayName: Provider<String> = extension.humanReadableName.map { name ->
        if (name.contains("Plugin", ignoreCase = true)) {
            "Jenkins $name"
        } else {
            "Jenkins $name Plugin"
        }
    }

}
