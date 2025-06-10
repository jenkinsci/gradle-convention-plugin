import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    id("org.jetbrains.kotlin.jvm")
}

configure<KotlinJvmProjectExtension> {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    explicitApi()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9)
        allWarningsAsErrors.set(true)

        progressiveMode.set(true)

        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xjvm-default=all",
            "-opt-in=kotlin.RequiresOptIn",
            "-Xtype-enhancement-improvements-strict-mode"
        )
    }
}

project.dependencies {
    val libs = the<VersionCatalogsExtension>().named("libs")

    add("implementation", platform(libs.findLibrary("kotlin-bom").get()))
    add("implementation", platform(libs.findLibrary("kotlin-stdlib").get()))
    add("implementation", platform(libs.findLibrary("kotlin-reflect").get()))
}