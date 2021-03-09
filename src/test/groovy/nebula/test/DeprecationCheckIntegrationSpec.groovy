package nebula.test

import spock.lang.IgnoreIf

class DeprecationCheckIntegrationSpec extends IntegrationSpec {
    @IgnoreIf({ jvm.isJava9Compatible() })
    def 'deprecation warnings cause test to fail'() {
        given:
        buildFile << """
            apply plugin: 'java'
            
            repositories {
                mavenCentral()
            }
            
            dependencies {
                implementation('com.google.guava:guava:19.0') {
                    force = true
                }
            }
        """

        when:
        runTasks('help')

        then:
        def e = thrown(IllegalArgumentException)
        e.message.startsWith('Deprecation warnings were found (Set the ignoreDeprecations system property during the test to ignore)')
    }
}
