package utils

import constants.PluginMetadata
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.util.GradleVersion

public object GradleVersionUtils {
    public fun verifyGradleVersion(project: Project) {
        val currentVersion = GradleVersion.current()
        val requiredVersion = GradleVersion.version(PluginMetadata.MIN_GRADLE_VERSION)

        if (currentVersion < requiredVersion) {
            throw GradleException(
                "${PluginMetadata.DISPLAY_NAME} requires Gradle ${PluginMetadata.VERSION} or higher. " +
                    "Current version is ${currentVersion.version}. Please upgrade your Gradle version.",
            )
        }
    }
}
