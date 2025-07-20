/*
 * Copyright 2025 Aarav Mahajan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package constants

public object ConfigurationConstants {
    private const val PLUGIN_PREFIX = "cfg.plg"
    private const val BOM_PREFIX = "cfg.bom"
    private const val QUALITY_PREFIX = "cfg.quality"

    // Jenkins
    public const val JENKINS_VERSION: String = "$PLUGIN_PREFIX.jenkinsVersion"
    public const val PLUGIN_ID: String = "$PLUGIN_PREFIX.pluginId"
    public const val GROUP_ID: String = "$PLUGIN_PREFIX.groupId"
    public const val DESCRIPTION: String = "$PLUGIN_PREFIX.description"
    public const val MINIMUM_JENKINS_VERSION: String = "$PLUGIN_PREFIX.minimumJenkinsCoreVersion"
    public const val SANDBOXED: String = "$PLUGIN_PREFIX.sandboxed"
    public const val USE_PLUGIN_FIRST_CLASS_LOADER: String = "$PLUGIN_PREFIX.usePluginFirstClassLoader"
    public const val GENERATE_TESTS: String = "$PLUGIN_PREFIX.generateTests"

    // BOM
    public const val JENKINS_BOM: String = "$BOM_PREFIX.jenkins.enabled"
    public const val GROOVY_BOM: String = "$BOM_PREFIX.groovy.enabled"
    public const val JACKSON_BOM: String = "$BOM_PREFIX.jackson.enabled"
    public const val SPRING_BOM: String = "$BOM_PREFIX.spring.enabled"
    public const val NETTY_BOM: String = "$BOM_PREFIX.netty.enabled"
    public const val SLF4J_BOM: String = "$BOM_PREFIX.slf4j.enabled"
    public const val JETTY_BOM: String = "$BOM_PREFIX.jetty.enabled"
    public const val GUAVA_VERSION: String = "$BOM_PREFIX.guava.enabled"
    public const val LOG4J_VERSION: String = "$BOM_PREFIX.log4j.enabled"
    public const val VERTX_VERSION: String = "$BOM_PREFIX.vertx.enabled"
    public const val JUNIT_BOM: String = "$BOM_PREFIX.junit.enabled"
    public const val MOCKITO_BOM: String = "$BOM_PREFIX.mockito.enabled"
    public const val TESTCONTAINERS_BOM: String = "$BOM_PREFIX.testContainers.enabled"

    // Quality
    public const val CHECKSTYLE_ENABLED: String = "$QUALITY_PREFIX.checkstyle.enabled"
    public const val CHECKSTYLE_VERSION: String = "$QUALITY_PREFIX.checkstyle.version"
    public const val SPOTBUGS_ENABLED: String = "$QUALITY_PREFIX.spotbugs.enabled"
    public const val SPOTBUGS_VERSION: String = "$QUALITY_PREFIX.spotbugs.version"
    public const val PMD_ENABLED: String = "$QUALITY_PREFIX.pmd.enabled"
    public const val PMD_VERSION: String = "$QUALITY_PREFIX.pmd.version"
    public const val JACOCO_ENABLED: String = "$QUALITY_PREFIX.jacoco.enabled"
    public const val JACOCO_VERSION: String = "$QUALITY_PREFIX.jacoco.version"
    public const val JACOCO_COVERAGE_MINIMUM: String = "$QUALITY_PREFIX.jacoco.coverage.minimum"
    public const val DETEKT_ENABLED: String = "$QUALITY_PREFIX.detekt.enabled"
    public const val DETEKT_VERSION: String = "$QUALITY_PREFIX.detekt.version"
    public const val SPOTLESS_ENABLED: String = "$QUALITY_PREFIX.spotless.enabled"
    public const val OWASP_ENABLED: String = "$QUALITY_PREFIX.owasp.enabled"
    public const val OWASP_FAIL_CVSS: String = "$QUALITY_PREFIX.owasp.failOnCvss"
    public const val PITEST_ENABLED: String = "$QUALITY_PREFIX.pitest.enabled"
    public const val PITEST_VERSION: String = "$QUALITY_PREFIX.pitest.version"
    public const val PITEST_MUTATION_THRESHOLD: String = "$QUALITY_PREFIX.pitest.mutationThreshold"
    public const val KOVER_ENABLED: String = "$QUALITY_PREFIX.kover.enabled"
    public const val KOVER_THRESHOLD: String = "$QUALITY_PREFIX.kover.coverageThreshold"
    public const val ESLINT_ENABLED: String = "$QUALITY_PREFIX.eslint.enabled"
    public const val DOKKA_ENABLED: String = "$QUALITY_PREFIX.dokka.enabled"
    public const val CODENARC_ENABLED: String = "$QUALITY_PREFIX.codenarc.enabled"
    public const val CODENARC_VERSION: String = "$QUALITY_PREFIX.codenarc.version"
}
