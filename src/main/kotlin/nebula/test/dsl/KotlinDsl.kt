package nebula.test.dsl

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

/**
 * Marks a method as part of the Kotlin DSL for [TestProjectBuilder]
 */
@DslMarker
annotation class NebulaTestKitDsl

@NebulaTestKitDsl
fun TestProjectBuilder.properties(config: ProjectProperties.() -> Unit) {
    properties().apply(config)
}

@NebulaTestKitDsl
fun TestProjectBuilder.rootProject(config: ProjectBuilder.() -> Unit) {
    rootProject().apply(config)
}

@NebulaTestKitDsl
fun TestProjectBuilder.settings(config: SettingsBuilder.() -> Unit) {
    settings().apply(config)
}

@NebulaTestKitDsl
fun SettingsBuilder.pluginManagement(config: PluginManagementBuilder.() -> Unit) {
    pluginManagement().apply(config)
}

@NebulaTestKitDsl
fun PluginManagementBuilder.repositories(config: RepositoriesBuilder.() -> Unit) {
    repositories ().apply(config)
}

@NebulaTestKitDsl
fun PluginManagementBuilder.plugins(config: PluginsBuilder.() -> Unit) {
    plugins().apply(config)
}

@NebulaTestKitDsl
fun SettingsBuilder.plugins(config: PluginsBuilder.() -> Unit) {
    plugins().apply(config)
}
@NebulaTestKitDsl
fun ProjectBuilder.plugins(config: PluginsBuilder.() -> Unit) {
    plugins().apply(config)
}

@NebulaTestKitDsl
fun ProjectBuilder.repositories(config: RepositoriesBuilder.() -> Unit) {
    repositories().apply(config)
}

@NebulaTestKitDsl
infix fun Plugin.version(version: String) {
    version(version)
}

@NebulaTestKitDsl
fun ProjectBuilder.src(config: SourcesBuilder.() -> Unit) {
    src().apply(config)
}

/**
 * Create source files in a custom source set
 * @param name name of the source set
 * @param config closure for configuration of the source set
 */
@NebulaTestKitDsl
fun SourcesBuilder.sourceSet(name: String, config: SourceSetBuilder.() -> Unit) {
    sourceSet(name).apply(config)
}

@NebulaTestKitDsl
fun SourcesBuilder.main(config: SourceSetBuilder.() -> Unit) {
    main().apply(config)
}

@NebulaTestKitDsl
fun SourcesBuilder.test(config: SourceSetBuilder.() -> Unit) {
    test().apply(config)
}

@NebulaTestKitDsl
fun TestProjectBuilder.subProject(name: String, config: ProjectBuilder.() -> Unit) {
    subProject(name).apply(config)
}

@NebulaTestKitDsl
fun SourceSetBuilder.java(fileName: String, source: () -> String) {
    java(fileName, source())
}

/**
 * Run a build with expectation of success.
 * This method will throw an exception if the build fails.
 */
fun TestProjectRunner.run(vararg args: String, customizer: GradleRunner.() -> Unit): BuildResult {
    return run(GradleRunner.create().apply(customizer), args.asList())
}

/**
 * Run a build with expectation of failure.
 * This method will throw an exception if the build succeeds.
 */
fun TestProjectRunner.runAndFail(vararg args: String, customizer: GradleRunner.() -> Unit): BuildResult {
    return runAndFail(GradleRunner.create().apply(customizer), args.asList())
}
