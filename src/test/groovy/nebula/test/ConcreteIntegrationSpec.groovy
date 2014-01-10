package nebula.test

import org.gradle.BuildResult

class ConcreteIntegrationSpec extends IntegrationSpec {
    def 'runs build'() {
        when:
        BuildResult buildResult = runTasks('dependencies')

        then:
        buildResult.failure == null
        buildResult.gradle != null
    }

    def 'setup and run build'() {
        writeHelloWorld('nebula.hello')
        buildFile << '''
            apply plugin: 'java'
        '''.stripIndent()

        when:
        runTasksSuccessfully('build')

        then:
        fileExists('build/classes/main/nebula/hello/HelloWorld.class')
        getStandardOutput().contains(':compileTestJava')
    }
}
