package nebula.test.dsl

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

import java.util.function.Supplier

/**
 * Extension methods for idiomatic Groovy DSL usage.
 */
class GroovyDsl {
    static void rootProject(TestProjectBuilder self, @DelegatesTo(ProjectBuilder) Closure config) {
        self.rootProject().with(config)
    }

    static void settings(TestProjectBuilder self, @DelegatesTo(SettingsBuilder) Closure config) {
        self.settings().with(config)
    }

    static void pluginManagement(SettingsBuilder self, @DelegatesTo(PluginManagementBuilder) Closure config) {
        self.pluginManagement().with(config)
    }

    static void repositories(PluginManagementBuilder self, @DelegatesTo(RepositoriesBuilder) Closure config) {
        self.repositories().with(config)
    }

    static void plugins(PluginManagementBuilder self, @DelegatesTo(PluginsBuilder) Closure config) {
        self.plugins().with(config)
    }

    static void plugins(SettingsBuilder self, @DelegatesTo(PluginsBuilder) Closure config) {
        self.plugins().with(config)
    }

    static void subProject(TestProjectBuilder self, String name, @DelegatesTo(ProjectBuilder) Closure config) {
        self.subProject(name).with(config)
    }

    static void repositories(ProjectBuilder self, @DelegatesTo(RepositoriesBuilder) Closure config) {
        self.repositories().with(config)
    }

    static void plugins(ProjectBuilder self, @DelegatesTo(PluginsBuilder) Closure config) {
        self.plugins().with(config)
    }

    static void src(ProjectBuilder self, @DelegatesTo(SourcesBuilder) Closure config) {
        self.src().with(config)
    }

    /**
     * Create source files in a custom source set
     * @param self self-reference for DSL
     * @param name name of the source set
     * @param config closure for configuration of the source set
     */
    static void sourceSet(SourcesBuilder self, String name, @DelegatesTo(SourceSetBuilder) Closure config) {
        self.sourceSet(name).with(config)
    }

    static void main(SourcesBuilder self, @DelegatesTo(SourceSetBuilder) Closure config) {
        self.main().with(config)
    }

    static void test(SourcesBuilder self, @DelegatesTo(SourceSetBuilder) Closure config) {
        self.test().with(config)
    }

    static void java(SourceSetBuilder self, String name, Supplier<String> source) {
        self.java(name, source.get())
    }

    /**
     * Run a build with expectation of success.
     * This method will throw an exception if the build fails.
     */
    static BuildResult run(TestProjectRunner self, List<String> args, @DelegatesTo(GradleRunner) Closure config) {
        final var runner = GradleRunner.create()
        runner.with(config)
        return self.run(runner, args)
    }

    /**
     * Run a build with expectation of failure.
     * This method will throw an exception if the build succeeds.
     */
    static BuildResult runAndFail(TestProjectRunner self, List<String> args, @DelegatesTo(GradleRunner) Closure config) {
        final var runner = GradleRunner.create()
        runner.with(config)
        return self.runAndFail(runner, args)
    }
}
