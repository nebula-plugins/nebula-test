package nebula.test.dsl

import nebula.test.SupportedGradleVersion
import org.gradle.testkit.runner.TaskOutcome
import spock.lang.Specification
import spock.lang.TempDir
import spock.lang.Unroll

import static nebula.test.dsl.TestKitAssertions.assertThat

class GroovyDslSpockTest extends Specification {
    @TempDir
    File testProjectDir

    @Unroll
    void "test groovy DSL with spock"() {
        setup:
        final var runner = GroovyTestProjectBuilder.testProject(testProjectDir) {
            settings {
                plugins {
                    id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
                }
            }
            rootProject {
                plugins {
                    java()
                }
                javaToolchain(javaVersion)
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

        when:
        final var result = runner.run(["build"]) {
            withGradleVersion(gradle.version)
            forwardOutput()
        }

        then:
        assertThat(result)
                .hasNoDeprecationWarnings()
                .hasNoMutableStateWarnings()
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS)

        where:
        javaVersion | gradle
        11          | SupportedGradleVersion.MIN
        17          | SupportedGradleVersion.MAX
    }
}
