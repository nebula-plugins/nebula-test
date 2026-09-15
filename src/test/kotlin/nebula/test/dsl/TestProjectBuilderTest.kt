package nebula.test.dsl

import nebula.test.SupportedGradleVersion
import nebula.test.dsl.TestKitAssertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.File

internal class TestProjectBuilderTest {
    @TempDir
    lateinit var testProjectDir: File

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion::class)
    fun `test composite build`(gradleVersion: SupportedGradleVersion) {
        val runner = testProject(testProjectDir) {
            properties {
                configurationCache(true)
                buildCache(true)
            }
            settings {
                name("library")
            }
            includedBuild("sub1") {
                subProject("sub2") {
                    examplePluginProject()
                }
            }
            rootProject {
                plugins {
                    java()
                    id("org.example.myplugin")
                }
            }
        }

        val result = runner.run("build") {
            forwardOutput()
            withGradle(gradleVersion.version)
        }
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result).task(":sub1:sub2:jar").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result)
            .hasNoDeprecationWarnings()
            .hasNoMutableStateWarnings()
    }

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion::class)
    fun `test composite plugin build`(gradleVersion: SupportedGradleVersion) {
        val runner = testProject(testProjectDir) {
            properties {
                configurationCache(true)
                buildCache(true)
            }
            settings {
                name("library")
            }
            includedPluginBuild("sub1") {
                subProject("sub2") {
                    examplePluginProject()
                }
            }
            rootProject {
                plugins {
                    java()
                    id("org.example.myplugin")
                }
            }
        }

        val result = runner.run("buildEnvironment") {
            forwardOutput()
            withGradle(gradleVersion.version)
        }
        assertThat(result).task(":sub1:sub2:jar")
            .`as`("included build will run even with no dependency")
            .hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result)
            .hasNoProblemsReport()
            .hasNoDeprecationWarnings()
            .hasNoMutableStateWarnings()
    }
}