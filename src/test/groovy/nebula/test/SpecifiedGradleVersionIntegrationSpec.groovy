package nebula.test

import org.gradle.api.logging.LogLevel
import spock.lang.Ignore
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

    static final String CUSTOM_DISTRIBUTION = 'http://dl.bintray.com/nebula/gradle-distributions/1.12-20140608201532+0000/gradle-1.12-20140608201532+0000-bin.zip'

    @Ignore("Only works with a custom distribution that is compatible with our runtime, of which 1.12 is not compatible with our spock 2.0 dependency")
    def 'should be able to use custom distribution'() {
        buildFile << '''
                task showVersion << {
                   println "Gradle Version: ${gradle.gradleVersion}"
                }
            '''.stripIndent()
        File wrapperProperties = new File(projectDir, 'gradle/wrapper/gradle-wrapper.properties')
        wrapperProperties.parentFile.mkdirs()
        wrapperProperties << """
            #Tue Jun 03 14:28:56 PDT 2014
            distributionBase=GRADLE_USER_HOME
            distributionPath=wrapper/dists
            zipStoreBase=GRADLE_USER_HOME
            zipStorePath=wrapper/dists
            distributionUrl=${CUSTOM_DISTRIBUTION}
        """

        when:
        def result = runTasksSuccessfully('showVersion')

        then:
        result.standardOutput.contains("Gradle Version: 1.12-20140608201532+0000")
    }

    @Ignore("Only works with a custom distribution that is compatible with our runtime, of which 1.12 is not compatible with our spock 2.0 dependency")
    def 'should be able to use custom distribution in a test'() {
        def testFile = new File(projectDir, "src/test/groovy/testing/DistributionTest.groovy")
        testFile.parentFile.mkdirs()
        testFile << '''
            package testing

            import nebula.test.IntegrationSpec
            import nebula.test.functional.ExecutionResult

            class DistributionTest extends IntegrationSpec {
                def 'confirm distribution'() {
                    buildFile << """
                        task print << {
                            println "Gradle Inner Test Version: \\${gradle.gradleVersion}"
                        }
                    """.stripIndent()
                    expect:
                    runTasksSuccessfully('print')
                }
            }
            '''.stripIndent()
        buildFile << '''
            apply plugin: 'groovy'
            dependencies {
                testCompile localGroovy()
            }
            sourceSets.test.compileClasspath += [buildscript.configurations.classpath]
            test {
                classpath += [buildscript.configurations.classpath]
                testLogging {
                    events "passed", "skipped", "failed", "standardOut", "standardError"
                }
            }
            '''.stripIndent()
        writeHelloWorld('testing')

        File wrapperProperties = new File(projectDir, 'gradle/wrapper/gradle-wrapper.properties')
        wrapperProperties.parentFile.mkdirs()
        wrapperProperties << """
            #Tue Jun 03 14:28:56 PDT 2014
            distributionBase=GRADLE_USER_HOME
            distributionPath=wrapper/dists
            zipStoreBase=GRADLE_USER_HOME
            zipStorePath=wrapper/dists
            distributionUrl=${CUSTOM_DISTRIBUTION}
        """.stripIndent()

        when:
        def result = runTasksSuccessfully('test')

        then:
        result.standardOutput.contains("Gradle Inner Test Version: 1.12-20140608201532+0000")
    }
}
