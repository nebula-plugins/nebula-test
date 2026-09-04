package nebula.test.dsl

import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

import java.util.function.Consumer

/**
 * Extension methods for idiomatic Groovy DSL usage.
 */
class GroovyDsl {
    static void properties(TestProjectBuilder self, @DelegatesTo(ProjectProperties) Closure config) {
        self.properties().with(config)
    }

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

    static void includedBuild(TestProjectBuilder self, String name, @DelegatesTo(TestProjectBuilder) Closure config) {
        self.includedBuild(name).with(config)
    }

    static void subProject(TestProjectBuilder self, String name, @DelegatesTo(ProjectBuilder) Closure config) {
        self.subProject(name).with(config)
    }

    static void subProject(TestProjectBuilder self, String name, String path, @DelegatesTo(ProjectBuilder) Closure config) {
        self.subProject(name, path).with(config)
    }

    static void buildscript(ProjectBuilder self, @DelegatesTo(BuildscriptBuilder) Closure config) {
        self.buildscript().with(config)
    }

    static void repositories(ProjectBuilder self, @DelegatesTo(RepositoriesBuilder) Closure config) {
        self.repositories().with(config)
    }

    static void dependencies(ProjectBuilder self, @DelegatesTo(DependenciesBuilder) Closure config) {
        self.dependencies().with(config)
    }

    static DependenciesBuilder.SpecialDependency project(ProjectBuilder self, String projectPath) {
        return self.dependencies().project(projectPath)
    }

    static DependenciesBuilder.SpecialDependency platform(ProjectBuilder self, String notation) {
        return self.dependencies().platform(notation)
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

    static void testing(ProjectBuilder self, @DelegatesTo(TestingBuilder) Closure config) {
        self.testing().with(config)
    }

    static void suites(TestingBuilder self, @DelegatesTo(TestingSuitesBuilder) Closure config) {
        self.suites().with(config)
    }

    static void create(TestingSuitesBuilder self, String name, @DelegatesTo(JvmTestSuiteBuilder) Closure config) {
        self.create(name).with(config)
    }

    static void named(TestingSuitesBuilder self, String name, @DelegatesTo(JvmTestSuiteBuilder) Closure config) {
        self.named(name).with(config)
    }

    static void test(TestingSuitesBuilder self, @DelegatesTo(JvmTestSuiteBuilder) Closure config) {
        self.test().with(config)
    }

    static void withGradle(GradleRunner self, Gradle gradleVersion) {
        switch (gradleVersion) {
            case Gradle.GradleVersion -> self.withGradleVersion(gradleVersion.version())
            case Gradle.GradleDistribution -> self.withGradleDistribution(URI.create(gradleVersion.url()))
            default -> {
            }
        }
    }

    /**
     * Run a build with expectation of success.
     * This method will throw an exception if the build fails.
     */
    @CompileStatic
    static BuildResult run(TestProjectRunner self, List<String> args, @DelegatesTo(GradleRunner) Closure config) {
        Consumer<GradleRunner> consumer = {
            config.setDelegate(it)
            config()
        }
        return self.run(args, consumer)
    }

    /**
     * Run a build with expectation of failure.
     * This method will throw an exception if the build succeeds.
     */
    static BuildResult runAndFail(TestProjectRunner self, List<String> args, @DelegatesTo(GradleRunner) Closure config) {
        Consumer<GradleRunner> consumer = {
            config.setDelegate(it)
            config()
        }
        return self.runAndFail(args, consumer)
    }
}
