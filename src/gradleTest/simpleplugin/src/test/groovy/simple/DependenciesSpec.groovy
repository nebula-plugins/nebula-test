package simple

import nebula.test.IntegrationSpec
import nebula.test.dependencies.DependencyGraphBuilder
import nebula.test.dependencies.GradleDependencyGenerator
import nebula.test.dependencies.ModuleBuilder

class DependenciesSpec extends IntegrationSpec {
    def 'generate some dependencies'() {
        def graph = new DependencyGraphBuilder()
                .addModule('testjava:a:0.1.0')
                .addModule(new ModuleBuilder('testjava:b:0.1.0')
                            .addDependency('testjava:a:0.1.0').build())
                .addModule('testjava:c:0.1.0').build()
        File mavenrepo = new GradleDependencyGenerator(graph, "${projectDir}/testrepogen").generateTestMavenRepo()

        buildFile << """\
            apply plugin: 'war'
            repositories {
                maven { url = '${mavenrepo.absolutePath}' }
            }
            dependencies {
                compile 'testjava:a:0.1.0'
                compile 'testjava:b:0.1.0'
                compile 'testjava:c:0.1.0'
            }
        """.stripIndent()

        when:
        def result = runTasksSuccessfully('dependencies')

        then:
        String dependencies = '''\
            +--- testjava:a:0.1.0
            +--- testjava:b:0.1.0
            |    \\--- testjava:a:0.1.0
            \\--- testjava:c:0.1.0
        '''.stripIndent()

        result.standardOutput.contains(dependencies)
    }
}
