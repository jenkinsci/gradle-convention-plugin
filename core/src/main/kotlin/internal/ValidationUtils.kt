package internal

import java.net.URI

internal object ValidationUtils {

    fun isValidPluginId(pluginId: String): Boolean {
        val pattern = Regex("^[a-z0-9]+(?:-[a-z0-9]+)*$")
        return pattern.matches(pluginId) && !pluginId.contains("jenkins") && !pluginId.contains("plugin")
    }

    fun isValidJenkinsVersion(version: String): Boolean {
        return version.matches(Regex("^\\d+\\.\\d{3}\\.\\d+$"))
    }

    fun isValidUrl(url: URI): Boolean {
        return try {
            url.scheme?.let { it == "http" || it == "https" } ?: false
        } catch (_: Exception) {
            false
        }
    }

    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$"))
    }

    private const val MIN_SUPPORTED_MAJOR = 2
    private const val MIN_SUPPORTED_MINOR = 492

    fun isJenkinsVersionTooOld(version: String): Boolean {

        val parts = version.split(".")
        if (parts.size < MIN_SUPPORTED_MAJOR) {
            return true
        }

        return runCatching {
            val major = parts[0].toInt()
            val minor = parts[1].toInt()

            major < MIN_SUPPORTED_MAJOR || (major == MIN_SUPPORTED_MAJOR && minor < MIN_SUPPORTED_MINOR)

        }.getOrElse { true }
    }

}
