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
package io.github.aaravmahajanofficial.constants

public object ConfigurationConstants {
    public object Plugin {
        public const val JENKINS_VERSION: String = "jenkinsVersion"
        public const val TEST_JVM_ARGS: String = "test-jvm-args"
    }

    public object Bom {
        private const val BOM_PREFIX = "bom"
        public const val JENKINS_BOM: String = "$BOM_PREFIX.jenkins.enabled"
        public const val GROOVY_BOM: String = "$BOM_PREFIX.groovy.enabled"
        public const val JACKSON_BOM: String = "$BOM_PREFIX.jackson.enabled"
        public const val SPRING_BOM: String = "$BOM_PREFIX.spring.enabled"
        public const val NETTY_BOM: String = "$BOM_PREFIX.netty.enabled"
        public const val SLF4J_BOM: String = "$BOM_PREFIX.slf4j.enabled"
        public const val JETTY_BOM: String = "$BOM_PREFIX.jetty.enabled"
        public const val GUAVA_BOM: String = "$BOM_PREFIX.guava.enabled"
        public const val LOG4J_BOM: String = "$BOM_PREFIX.log4j.enabled"
        public const val VERTX_BOM: String = "$BOM_PREFIX.vertx.enabled"
        public const val JUNIT_BOM: String = "$BOM_PREFIX.junit.enabled"
        public const val MOCKITO_BOM: String = "$BOM_PREFIX.mockito.enabled"
        public const val TESTCONTAINERS_BOM: String = "$BOM_PREFIX.testContainers.enabled"
        public const val SPOCK_BOM: String = "$BOM_PREFIX.spock.enabled"
    }

    public object Quality {
        private const val QUALITY_PREFIX = "quality"
        public const val ENABLE_QUALITY_TOOLS: String = "$QUALITY_PREFIX.enabled"
        public const val CHECKSTYLE_ENABLED: String = "$QUALITY_PREFIX.checkstyle.enabled"
        public const val SPOTBUGS_ENABLED: String = "$QUALITY_PREFIX.spotbugs.enabled"
        public const val PMD_ENABLED: String = "$QUALITY_PREFIX.pmd.enabled"
        public const val JACOCO_ENABLED: String = "$QUALITY_PREFIX.jacoco.enabled"
        public const val JACOCO_COVERAGE_MINIMUM: String = "$QUALITY_PREFIX.jacoco.coverage.minimum"
        public const val DETEKT_ENABLED: String = "$QUALITY_PREFIX.detekt.enabled"
        public const val SPOTLESS_ENABLED: String = "$QUALITY_PREFIX.spotless.enabled"
        public const val OWASP_ENABLED: String = "$QUALITY_PREFIX.owasp.enabled"
        public const val OWASP_FAIL_CVSS: String = "$QUALITY_PREFIX.owasp.failOnCvss"
        public const val PITEST_ENABLED: String = "$QUALITY_PREFIX.pitest.enabled"
        public const val PITEST_MUTATION_THRESHOLD: String = "$QUALITY_PREFIX.pitest.mutationThreshold"
        public const val KOVER_ENABLED: String = "$QUALITY_PREFIX.kover.enabled"
        public const val KOVER_THRESHOLD: String = "$QUALITY_PREFIX.kover.coverageThreshold"
        public const val ESLINT_ENABLED: String = "$QUALITY_PREFIX.eslint.enabled"
        public const val DOKKA_ENABLED: String = "$QUALITY_PREFIX.dokka.enabled"
        public const val CODENARC_ENABLED: String = "$QUALITY_PREFIX.codenarc.enabled"
        public const val CPD_ENABLED: String = "$QUALITY_PREFIX.cpd.enabled"
    }
}
