package nebula.test

import nebula.test.functional.ExecutionResult
import spock.lang.Unroll

class ConcreteIntegrationSpec extends IntegrationSpec {
    def 'runs build'() {
        when:
        ExecutionResult buildResult = runTasks('dependencies')

        then:
        buildResult.failure == null
    }

    def 'setup and run build'() {
        buildFile << '''
            apply plugin: 'java'
        '''.stripIndent()

        when:
        writeHelloWorld('nebula.test.hello')

        then:
        fileExists('src/main/java/nebula/test/hello/HelloWorld.java')

        when:
        def result = runTasksSuccessfully('build', '--info')

        then:
        fileExists('build/classes/main/nebula/test/hello/HelloWorld.class')
        result.wasExecuted(':compileTestJava')
        result.getStandardOutput().contains('Skipping task \':compileTestJava\' as it has no source files.')
    }


    @Unroll
    def 'can import from classpath using #desc #testTooling'(String desc, boolean testTooling) {
        buildFile << '''
            import nebula.test.FakePlugin
            apply plugin: FakePlugin
        '''.stripIndent()

        when:
        runTasksSuccessfully('tasks')

        then:
        noExceptionThrown()

        where:
        desc       | testTooling
        "Tooling"  | true
        "Launcher" | false
    }
}
