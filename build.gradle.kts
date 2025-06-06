plugins {
    id("jenkins-convention-common") apply false
}

allprojects {
    group = "io.jenkins.gradle"
    version = "1.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "jenkins-convention-common")
}