package nebula.test.dsl;

import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static nebula.test.dsl.TestKitAssertions.assertThat

class GroovyDslTest {
    @TempDir
    File testProjectDir

    @Test
    void testGroovyDsl() {
        final var runner = GroovyTestProjectBuilder.testProject(testProjectDir) {
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

        final var result = runner.run(["build"]) {
            withGradleVersion("8.14.1")
            forwardOutput()
        }

        assertThat(result)
                .hasNoDeprecationWarnings()
                .hasNoMutableStateWarnings()
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void testMultiProject() {
        final var runner = GroovyTestProjectBuilder.testProject(testProjectDir) {
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
        }

        final var result = runner.run(["build"]) {
            forwardOutput()
        }

        assertThat(result)
                .hasNoDeprecationWarnings()
                .hasNoMutableStateWarnings()
        assertThat(result).task(":sub1:compileJava").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result).task(":sub1:build").hasOutcome(TaskOutcome.SUCCESS)
    }
}
