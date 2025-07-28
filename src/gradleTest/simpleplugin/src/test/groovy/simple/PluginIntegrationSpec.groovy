package simple

import nebula.test.dsl.GroovyTestProjectBuilder
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static nebula.test.dsl.TestKitAssertions.assertThat

class PluginIntegrationSpec extends Specification {
    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    def "testing a plugin"() {
        setup:
        final var runner = GroovyTestProjectBuilder.testProject(testProjectDir.root) {
            rootProject {
                plugins {
                    id("test.simple-plugin")
                }
            }
        }

        when:
        final var result = runner.run(["sampleTask"]) {
            forwardOutput()
            withGradleVersion(gradleVersion)
        }

        then:
        assertThat(result).task(":sampleTask").hasOutcome(TaskOutcome.SUCCESS, TaskOutcome.UP_TO_DATE)

        where:
        gradleVersion << ["8.11.1", "9.0.0-rc-4"]
    }
}
