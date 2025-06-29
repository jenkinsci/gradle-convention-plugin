plugins {
    base
    alias(libs.plugins.kotlin.jvm) apply false
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = libs.versions.gradle.get()
    distributionType = Wrapper.DistributionType.ALL
}
