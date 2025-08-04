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
}
