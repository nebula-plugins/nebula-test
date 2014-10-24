package nebula.test

import org.gradle.api.logging.LogLevel
import spock.lang.Unroll

class SpecifiedGradleVersionIntegrationSpec extends IntegrationSpec {
    def setup() {
        fork = true
    }

    @Unroll("should use Gradle #requestedGradleVersion when requested")
    def "should allow to run functional tests with different Gradle versions"() {
        given:
            writeHelloWorld('nebula.test.hello')
            buildFile << '''
                apply plugin: 'java'
            '''.stripIndent()
        and:
            logLevel = LogLevel.DEBUG
        and:
            gradleVersion = requestedGradleVersion
        when:
            def result = runTasksSuccessfully('build')
        then:
            result.standardOutput.contains("gradle/$requestedGradleVersion/taskArtifacts")
        where:
            requestedGradleVersion << ['2.0', '2.1']
    }
}
