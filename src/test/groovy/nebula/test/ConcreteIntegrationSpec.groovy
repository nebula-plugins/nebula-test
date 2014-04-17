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
        buildFile << '''
            apply plugin: 'java'
        '''.stripIndent()

        when:
        writeHelloWorld('nebula.test.hello')

        then:
        fileExists('src/main/java/nebula/test/hello/HelloWorld.java')

        when:
        runTasksSuccessfully('build')

        then:
        fileExists('build/classes/main/nebula/test/hello/HelloWorld.class')
        getStandardOutput().contains(':compileTestJava')
    }
}
