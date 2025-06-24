public object JenkinsPluginValidation {

    public data class ValidationResult(
        val errors: List<String> = emptyList(),
        val warnings: List<String> = emptyList(),
        val recommendations: List<String> = emptyList(),
    ) {
        val isValid: Boolean = errors.isEmpty()
        val hasWarnings: Boolean = warnings.isNotEmpty()
        val hasRecommendations: Boolean = recommendations.isNotEmpty()
    }

    public fun validate(extension: JenkinsConventionExtension): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        validatePluginId(extension, errors, warnings)
        validateVersions(extension, errors, warnings)
        validateDependencies(extension, warnings, recommendations)
        validateSecurity(extension, warnings, recommendations)

        return ValidationResult(errors, warnings, recommendations)
    }

    private fun validatePluginId(
        extension: JenkinsConventionExtension,
        errors: MutableList<String>,
        warnings: MutableList<String>,
    ) {
        val pluginId = extension.pluginId.orNull
        when {
            pluginId.isNullOrBlank() -> {
                errors.add("Plugin Id must not be empty")
            }

            !pluginId.matches(Regex("^[a-z0-9]+(?:-[a-z0-9]+)*$")) -> {
                errors.add("Plugin ID '$pluginId' must start with lowercase letter, contain only lowercase letters, digits,  numbers, or hyphens, and end with a lowercase letter or digit")
            }

            pluginId.length > 50 -> {
                warnings.add("Plugin ID '$pluginId is longer than 50 characters, which may cause issues")
            }

            pluginId.contains("jenkins") -> {
                warnings.add("Plugin ID contains 'jenkins' - this is usually redundant")
            }
        }

    }

    private fun validateVersions(
        extension: JenkinsConventionExtension,
        errors: MutableList<String>,
        warnings: MutableList<String>,
    ) {
        val minJenkinsVersion = extension.minimumJenkinsVersion.get()
        if (minJenkinsVersion != null) {
            val version = parseVersion(minJenkinsVersion)
            if (version == null) {
                errors.add("Invalid Jenkins version format: '$minJenkinsVersion'")
            } else {
                val currentLts = parseVersion(JenkinsConventions.CURRENT_JENKINS_LTS)!!
                if (compareVersions(version, currentLts) < 0) {
                    warnings.add("Minimum Jenkins version '$minJenkinsVersion' is older than current LTS '$currentLts'")
                }
            }

        }
    }

    private fun validateDependencies(
        extension: JenkinsConventionExtension,
        warnings: MutableList<String>,
        recommendations: MutableList<String>,
    ) {

        val dependencies = extension.pluginDependencies.get()

        val hasWorkflowStep = dependencies.any { it.pluginId.get().contains("workflow") }
        val isPipelinePlugin = extension.computed.isPipelinePlugin.get()

        if (isPipelinePlugin && !hasWorkflowStep) {
            recommendations.add("Pipeline plugins should typically depend workflow-step-api")
        }

    }

    private fun validateSecurity(
        extension: JenkinsConventionExtension,
        warnings: MutableList<String>,
        recommendations: MutableList<String>,
    ) {

        if (extension.requireEscapeByDefaultInJelly.orNull == false) {
            warnings.add("Escape-by-default in Jelly is disabled. This may introduce XSS vulnerabilities")
        }

        if (extension.sandboxed.orNull == false && extension.usePluginFirstClassLoader.orNull == true) {
            warnings.add("Plugin-first classloader with non-sandboxed plugin may cause compatibility issues")
        }

        val maskedClasses = extension.maskedClassesFromCore.get()
        if (maskedClasses.isNotEmpty()) {
            recommendations.add("Masked classes should be avoided when possible, since they can lead to compatibility problems.")
        }

    }

    private fun compareVersions(version1: List<Int>, version2: List<Int>): Int {
        val maxLength = maxOf(version1.size, version2.size)
        for (i in 0 until maxLength) {
            val v1 = version1.getOrElse(i) { 0 }
            val v2 = version2.getOrElse(i) { 0 }
            when {
                v1 < v2 -> -1
                v1 > v2 -> 1
            }
        }
        return 0
    }

    private fun parseVersion(version: String): List<Int>? {
        return try {
            version.split(".").map { it.toInt() }.takeIf { it.isNotEmpty() }
        } catch (_: NumberFormatException) {
            null
        }
    }
}
