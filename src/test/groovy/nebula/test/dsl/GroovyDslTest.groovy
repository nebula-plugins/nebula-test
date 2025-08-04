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
}
