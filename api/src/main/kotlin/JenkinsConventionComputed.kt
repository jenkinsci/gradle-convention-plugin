import org.gradle.api.provider.Provider
import java.net.URI

public class JenkinsConventionComputed(private val extension: JenkinsConventionExtension) {

    private val githubUrl: Provider<URI> = extension.scm.map { scm ->
        val connection = scm.connection.orNull

        when {
            connection?.contains("github.com") == true -> {
                val repoPattern = """github\.com[:/]([^/]+/[^/\.]+)""".toRegex()
                val match = repoPattern.find(connection)
                if (match != null) {
                    URI.create("github.com/${match.groupValues[1]}")
                } else {
                    scm.url.orNull ?: URI.create("https://github.com/jenkinsci/unknown")
                }
            }

            else -> scm.url.orNull ?: URI.create("https://github.com/jenkinsci/unknown")
        }
    }

    private val computedIssueTrackerUrl: Provider<URI> = githubUrl.map { github ->
        URI.create("${github}/issues")
    }

    private val computedDocumentationUrl: Provider<URI> = githubUrl.map { github ->
        URI.create("${github}/blob/main/README.md")
    }

    private val artifactId: Provider<String> = extension.pluginId

    private val groupId: Provider<String> = extension.pluginId.map {
        "org.jenkins-ci/plugins"
    }

    private val archiveBaseName: Provider<String> = extension.pluginId

    private val isPipelinePlugin: Provider<Boolean> = extension.pluginId.zip(extension.categories) { id, categories ->
        val deps = extension.dependencies.get()
        id.contains("pipeline") || id.contains("workflow") || categories.any { it.contains("pipeline") } || deps.any {
            it.pluginId.get().contains("workflow")
        }
    }

    private val isBuildPlugin: Provider<Boolean> = extension.pluginId.zip(extension.categories) { id, categories ->
        id.contains("build") || id.contains("gradle") || id.contains("maven") || id.contains("ant") || categories.any { it == "build" }
    }

    private val isScmPlugin: Provider<Boolean> = extension.pluginId.zip(extension.categories) { id, categories ->
        id.contains("git") || id.contains("svn") || id.contains("mercurial") || id.contains("scm") || id.contains("vcs") || id.contains(
            "github"
        ) || id.contains("bitbucket") || id.contains("gitlab") || id.contains("perforce") || categories.any { it == "scm" }
    }

}
