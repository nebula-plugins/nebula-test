package nebula.test.dsl

import nebula.test.SupportedGradleVersion
import nebula.test.dsl.TestKitAssertions.assertThat
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.io.File

internal class KotlinDslTest {
    @TempDir
    lateinit var testProjectDir: File

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion::class)
    fun `test single project build with sources`(gradleVersion: SupportedGradleVersion) {
        val runner = testProject(testProjectDir) {
            rootProject {
                plugins {
                    java()
                }
                mainSource()
            }
        }

        val result = runner.run("build") {
            forwardOutput()
            withGradleVersion(gradleVersion.version)
        }

        assertThat(result)
            .hasNoDeprecationWarnings()
            .hasNoMutableStateWarnings()
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS)
    }

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion::class)
    fun `test single project build with dependencies`(gradleVersion: SupportedGradleVersion) {
        val runner = testProject(testProjectDir) {
            settings {
                plugins {

                }
                pluginManagement {
                    plugins {

                    }
                    repositories {

                    }
                }
                name("library")
            }
            rootProject {
                plugins {
                    java()
                }
                repositories {
                    mavenCentral()
                }
                group("com.example")
                version("1.0.0")
                dependencies("""implementation("org.jspecify:jspecify:1.0.0")""")
                mainSource()
            }
        }

        val result = runner.run("build") {
            forwardOutput()
            withGradleVersion(gradleVersion.version)
        }

        assertThat(result)
            .hasNoDeprecationWarnings()
            .hasNoMutableStateWarnings()
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(testProjectDir.resolve("build/libs/library-1.0.0.jar")).exists()
    }


    @ParameterizedTest
    @EnumSource(SupportedGradleVersion::class)
    fun `test multi project build with sources`(gradleVersion: SupportedGradleVersion) {
        val runner = testProject(testProjectDir) {
            subProject("sub1") {
                plugins {
                    java()
                }
                src {
                    main {
                        java("Main.java") {
                            // language=java
                            """
public class Main {
    public static void main(String[] args) {
    }
}
"""

                        }
                    }
                }
            }
            subProject("sub2") {
                plugins {
                    java()
                }
                mainSource()
            }
        }

        val result = runner.run("build") {
            forwardOutput()
            withGradleVersion(gradleVersion.version)
        }

        assertThat(result.task(":sub1:compileJava")).hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result.task(":sub2:compileJava")).hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result.task(":sub1:build")).hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result.task(":sub1:build")).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    fun `test failing build`() {
        val runner = testProject(testProjectDir) {
            rootProject {
                plugins {
                    java()
                }
                src {
                    main {
                        java("Main.java") {
                            // language=java
                            """
public clss Main { // compile error
    public static void main(String[] args) {
    }
}
"""
                        }
                    }
                }
            }
        }

        val result = runner.runAndFail("build") {
            forwardOutput()
        }
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.FAILED)
    }

    @Test
    fun `test plugin with version`() {
        testProject(testProjectDir) {
            rootProject {
                plugins {
                    java()
                    id("org.springframework.boot") version "3.5.3"
                }
            }
        }
        assertThat(testProjectDir.toPath().resolve("build.gradle.kts"))
            .content().contains("""id("org.springframework.boot") version ("3.5.3")""")
    }

    @Test
    fun `test groovy script`() {
        testProject(testProjectDir, BuildscriptLanguage.GROOVY) {
            rootProject {
                plugins {
                    java()
                }
            }
        }
        assertThat(testProjectDir.toPath().resolve("build.gradle"))
            .exists()
            .content().contains("""id 'java'""")
    }

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion::class)
    fun `test properties and caching`(gradleVersion: SupportedGradleVersion) {
        val runner = testProject(testProjectDir) {
            properties {
                gradleCache(true)
                property("org.gradle.caching.debug", "true")
            }
            rootProject {
                plugins {
                    java()
                }
                mainSource()
            }
        }

        val result1 = runner.run("build", "--rerun-tasks") {
            forwardOutput()
            withGradleVersion(gradleVersion.version)
        }

        assertThat(result1).task(":compileJava").hasOutcome(TaskOutcome.SUCCESS)
        val result2 = runner.run("clean", "build") {
            forwardOutput()
            withGradleVersion(gradleVersion.version)
        }
        assertThat(result2).task(":compileJava").hasOutcome(TaskOutcome.FROM_CACHE)
    }

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion::class)
    fun `test rawBuildScript is additive`(gradleVersion: SupportedGradleVersion) {
        val runner = testProject(testProjectDir) {
            settings {
                name("library")
            }
            rootProject {
                mainSource()
                rawBuildScript("plugins { java }")
                rawBuildScript("repositories { mavenCentral() }")
                rawBuildScript("""group = "com.example"""")
                rawBuildScript("""version = "1.0.0"""")
            }
        }

        val result = runner.run("build") {
            forwardOutput()
            withGradleVersion(gradleVersion.version)
        }

        assertThat(result)
            .hasNoDeprecationWarnings()
            .hasNoMutableStateWarnings()
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(testProjectDir.resolve("build/libs/library-1.0.0.jar")).exists()
    }

    fun ProjectBuilder.mainSource(){
        src {
            main {
                java("Main.java") {
                    // language=java
                    """
public class Main {
    public static void main(String[] args) {
    }
}
"""
                }
            }
        }
    }
}
