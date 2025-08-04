package nebula.test.dsl

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

/**
 * Marks a method as part of the Kotlin DSL for [TestProjectBuilder]
 */
@DslMarker
annotation class NebulaTestKitDsl

@NebulaTestKitDsl
fun TestProjectBuilder.rootProject(config: ProjectBuilder.() -> Unit) {
    rootProject().apply(config)
}

@NebulaTestKitDsl
fun TestProjectBuilder.settings(config: SettingsBuilder.() -> Unit) {
    settings().apply(config)
}

@NebulaTestKitDsl
fun ProjectBuilder.plugins(config: PluginsBuilder.() -> Unit) {
    plugins().apply(config)
}

@NebulaTestKitDsl
fun ProjectBuilder.src(config: SourcesBuilder.() -> Unit) {
    src().apply(config)
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

fun TestProjectRunner.run(vararg args: String, customizer: GradleRunner.() -> Unit): BuildResult {
    return run(GradleRunner.create().apply(customizer), args.asList())
}