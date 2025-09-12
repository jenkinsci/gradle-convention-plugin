import com.vanniktech.maven.publish.VersionCatalog

plugins {
    `version-catalog`
    alias(baseLibs.plugins.maven.gradle.publish)
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
