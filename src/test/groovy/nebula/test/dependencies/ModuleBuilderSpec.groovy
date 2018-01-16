package nebula.test.dependencies

import spock.lang.Specification

class ModuleBuilderSpec extends Specification {
    def 'build module with no dependencies'() {
        def builder = new ModuleBuilder('test.modulebuilder:foo:1.0.0')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.group == 'test.modulebuilder'
        module.artifact == 'foo'
        module.version == '1.0.0'
        module.dependencies.size() == 0
    }

    def 'build module with no dependencies separate group, artifact, version'() {
        def builder = new ModuleBuilder('test.modulebuilder', 'foo', '1.0.0')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.group == 'test.modulebuilder'
        module.artifact == 'foo'
        module.version == '1.0.0'
        module.dependencies.size() == 0
    }

    def 'build module with specific status'() {
        def builder = new ModuleBuilder('test.modulebuilder', 'foo', '1.0.0').setStatus('snapshot')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.group == 'test.modulebuilder'
        module.artifact == 'foo'
        module.version == '1.0.0'
        module.status == 'snapshot'

    }

    def 'add dependency'() {
        def builder = new ModuleBuilder('test.modulebuilder', 'bar', '1.0.0')
        builder.addDependency('test.dependency', 'baz', '2.0.1')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.dependencies.size() == 1
        def dep = module.dependencies.find()
        dep.group == 'test.dependency'
        dep.artifact == 'baz'
        dep.version == '2.0.1'
    }

    def 'add dependencies'() {
        def builder = new ModuleBuilder('test.modulebuilder', 'bar', '1.0.0')
        builder.addDependency('test.dependency', 'baz', '2.0.1')
                .addDependency('test.dependency:qux:42.13.0')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.dependencies.size() == 2
        def baz = module.dependencies.find { it.artifact == 'baz' }
        baz.group == 'test.dependency'
        baz.artifact == 'baz'
        baz.version == '2.0.1'
        def qux = module.dependencies.find { it.artifact == 'qux' }
        qux.group == 'test.dependency'
        qux.artifact == 'qux'
        qux.version == '42.13.0'
    }
}
