import com.vanniktech.maven.publish.VersionCatalog

plugins {
    `version-catalog`
    alias(baseLibs.plugins.maven.gradle.publish.plugin)
}

catalog {
    versionCatalog {
        from(files("libs.versions.toml"))
    }
}

mavenPublishing {
    configure(VersionCatalog())
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
}
