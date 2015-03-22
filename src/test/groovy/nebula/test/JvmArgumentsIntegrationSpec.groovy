package nebula.test

class JvmArgumentsIntegrationSpec extends IntegrationSpec {

    private static final String TEST_JVM_ARGUMENT = "-XX:-PrintClassHistogram"

    def "should start Gradle with custom JVM argument in fork mode"() {
        given:
            writeHelloWorld('nebula.test.hello')
            buildFile << '''
                apply plugin: 'java'
            '''.stripIndent()
        and:
            fork = true
            jvmArguments = [TEST_JVM_ARGUMENT]
        when:
            def result = runTasksSuccessfully('compileJava')
        then:
            result.standardOutput.contains(TEST_JVM_ARGUMENT)
    }
}
