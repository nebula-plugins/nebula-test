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
                plugins{

                }
                pluginManagement {
                    plugins {

                    }
                    repositories {

                    }
                }
            }
            rootProject {
                plugins {
                    java()
                }
                repositories {
                    mavenCentral()
                }
                dependencies("""implementation("org.jspecify:jspecify:1.0.0")""")
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


    @Test
    fun `test multi project build with sources`() {
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

        val result = runner.run("build") {
            forwardOutput()
            withGradleVersion("8.14.1")
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
}
