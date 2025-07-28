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

    static void plugins(SettingsBuilder self, @DelegatesTo(PluginsBuilder) Closure config) {
        self.plugins().with(config)
    }

    static void plugins(ProjectBuilder self, @DelegatesTo(PluginsBuilder) Closure config) {
        self.plugins().with(config)
    }

    static void src(ProjectBuilder self, @DelegatesTo(SourcesBuilder) Closure config) {
        self.src().with(config)
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

    static BuildResult run(TestProjectRunner self, List<String> args, @DelegatesTo(GradleRunner) Closure config) {
        final var runner = GradleRunner.create()
        runner.with(config)
        return self.run(runner, args)
    }
}
