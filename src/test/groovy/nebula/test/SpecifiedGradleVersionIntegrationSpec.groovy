package nebula.test

import org.gradle.api.logging.LogLevel
import spock.lang.Unroll

class SpecifiedGradleVersionIntegrationSpec extends IntegrationSpec {

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
//            //TODO: How to get output before being connected to the daemon?with "Tooling API is using target Gradle version: 1.XX."?
//            result.getStandardOutput().contains("Tooling API is using target Gradle version: $requestedGradleVersion")
            result.getStandardOutput().contains("gradle/$requestedGradleVersion/taskArtifacts")
        where:
            requestedGradleVersion << ['1.12', '1.6']
    }
}
