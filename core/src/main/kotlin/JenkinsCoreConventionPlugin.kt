//import org.gradle.api.Plugin
//import org.gradle.api.Project
//import org.jenkinsci.gradle.plugins.jpi.JpiPlugin
//
//class JenkinsCoreConventionPlugin : Plugin<Project> {
//
//    override fun apply(project: Project) {
//
//        // required plugins
//        project.pluginManager.apply(JpiPlugin::class.java)
//        project.pluginManager.apply("java-library")
//
//        val extension = project.extensions.create<JenkinsPluginExtension>("jenkinsPlugin")
//
//
//    }
//
//}
//
//private fun configureJavaToolChain(project: Project, extension: JenkinsPluginExtension) {
//
//
//}