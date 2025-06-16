import common.javaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}

private val libs = the<VersionCatalogsExtension>().named("libs")

kotlin {
    jvmToolchain {
        languageVersion = javaLanguageVersion()
    }

    explicitApi()

    compilerOptions {
        apiVersion =
            org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1
        jvmTarget = JvmTarget.JVM_17

        allWarningsAsErrors.set(true)
        progressiveMode.set(true)

        optIn.add("kotlin.RequiresOptIn")
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xjvm-default=all",
        )
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(platform(libs.findLibrary("kotlin-bom").get()))
    implementation(libs.findLibrary("kotlin-reflect").get())
}
