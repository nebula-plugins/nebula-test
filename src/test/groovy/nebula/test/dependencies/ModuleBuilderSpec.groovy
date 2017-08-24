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
        module.classifier == null
        module.extension == null
        module.dependencies.size() == 0
    }

    def 'build module with classifier with no dependencies'() {
        def builder = new ModuleBuilder('test.modulebuilder:foo:1.0.0:bar')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.group == 'test.modulebuilder'
        module.artifact == 'foo'
        module.version == '1.0.0'
        module.classifier == 'bar'
        module.extension == null
        module.dependencies.size() == 0
    }


    def 'build module with extension with no dependencies'() {
        def builder = new ModuleBuilder('test.modulebuilder:foo:1.0.0@zip')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.group == 'test.modulebuilder'
        module.artifact == 'foo'
        module.version == '1.0.0'
        module.classifier == null
        module.extension == 'zip'
        module.dependencies.size() == 0
    }

    def 'build module with classifier and extension with no dependencies'() {
        def builder = new ModuleBuilder('test.modulebuilder:foo:1.0.0:bar@zip')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.group == 'test.modulebuilder'
        module.artifact == 'foo'
        module.version == '1.0.0'
        module.classifier == 'bar'
        module.extension == 'zip'
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
        module.classifier == null
        module.extension == null
        module.dependencies.size() == 0
    }


    def 'build module with no dependencies separate group, artifact, version, classifer, extension'() {
        def builder = new ModuleBuilder('test.modulebuilder', 'foo', '1.0.0', 'bar', 'zip')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.group == 'test.modulebuilder'
        module.artifact == 'foo'
        module.version == '1.0.0'
        module.classifier == 'bar'
        module.extension == 'zip'
        module.dependencies.size() == 0
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
        dep.classifier == null
        dep.extension == null
    }

    def 'add dependency with classifier, extension'() {
        def builder = new ModuleBuilder('test.modulebuilder', 'bar', '1.0.0')
        builder.addDependency('test.dependency', 'baz', '2.0.1', 'bar', 'zip')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.dependencies.size() == 1
        def dep = module.dependencies.find()
        dep.group == 'test.dependency'
        dep.artifact == 'baz'
        dep.version == '2.0.1'
        dep.classifier == 'bar'
        dep.extension == 'zip'
    }


    def 'add dependencies'() {
        def builder = new ModuleBuilder('test.modulebuilder', 'bar', '1.0.0')
        builder.addDependency('test.dependency', 'baz', '2.0.1')
                .addDependency('test.dependency:qux:42.13.0:bar@zip')

        when:
        DependencyGraphNode module = builder.build()

        then:
        module.dependencies.size() == 2
        def baz = module.dependencies.find { it.artifact == 'baz' }
        baz.group == 'test.dependency'
        baz.artifact == 'baz'
        baz.version == '2.0.1'
        baz.classifier == null
        baz.extension == null
        def qux = module.dependencies.find { it.artifact == 'qux' }
        qux.group == 'test.dependency'
        qux.artifact == 'qux'
        qux.version == '42.13.0'
        qux.classifier == 'bar'
        qux.extension == 'zip'
    }
}
