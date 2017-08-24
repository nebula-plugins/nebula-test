package nebula.test.dependencies

import spock.lang.Specification

class DependencyGraphBuilderSpec extends Specification {
    def 'add one dependency'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule('test.nebula:foo:1.0.0')

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 1
        Coordinate foo = graph.nodes.find().coordinate
        foo.group == 'test.nebula'
        foo.artifact == 'foo'
        foo.version == '1.0.0'
        foo.classifier == null
        foo.extension == null
    }

    def 'add one dependency with classifier'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule('test.nebula:foo:1.0.0:bar')

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 1
        Coordinate foo = graph.nodes.find().coordinate
        foo.group == 'test.nebula'
        foo.artifact == 'foo'
        foo.version == '1.0.0'
        foo.classifier == 'bar'
        foo.extension == null
    }

    def 'add one dependency with extension'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule('test.nebula:foo:1.0.0@zip')

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 1
        Coordinate foo = graph.nodes.find().coordinate
        foo.group == 'test.nebula'
        foo.artifact == 'foo'
        foo.version == '1.0.0'
        foo.classifier == null
        foo.extension == 'zip'
    }

    def 'add one dependency with classifier, extension'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule('test.nebula:foo:1.0.0:bar@zip')

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 1
        Coordinate foo = graph.nodes.find().coordinate
        foo.group == 'test.nebula'
        foo.artifact == 'foo'
        foo.version == '1.0.0'
        foo.classifier == 'bar'
        foo.extension == 'zip'
    }

    def 'add one dependency with group, artifact, version syntax'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule('test.nebula', 'foo', '1.0.0')

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 1
        Coordinate foo = graph.nodes.find().coordinate
        foo.group == 'test.nebula'
        foo.artifact == 'foo'
        foo.version == '1.0.0'
        foo.classifier == null
        foo.extension == null
    }

    def 'add one dependency with group, artifact, version, classifier, extension syntax'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule('test.nebula', 'foo', '1.0.0', 'bar', 'zip')

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 1
        Coordinate foo = graph.nodes.find().coordinate
        foo.group == 'test.nebula'
        foo.artifact == 'foo'
        foo.version == '1.0.0'
        foo.classifier == 'bar'
        foo.extension == 'zip'
    }

    def 'add multiple dependencies'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule('test.nebula:foo:1.0.0')
                .addModule('a.nebula:bar:2.0.0')

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 2
        Coordinate foo = graph.nodes.find { it.coordinate.artifact == 'foo' }.coordinate
        foo.group == 'test.nebula'
        foo.artifact == 'foo'
        foo.version == '1.0.0'
        foo.classifier == null
        foo.extension == null
        Coordinate bar = graph.nodes.find { it.coordinate.artifact == 'bar' }.coordinate
        bar.group == 'a.nebula'
        bar.artifact == 'bar'
        bar.version == '2.0.0'
        bar.classifier == null
        bar.extension == null
    }

    def 'add module with dependencies'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule(new ModuleBuilder('test.nebula:foo:1.0.0').addDependency('test.nebula:bar:1.1.1').build())

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 2
        graph.nodes.find { it.artifact == 'bar' } != null
    }

    def 'add module with dependencies, add another module make sure it replaces with the one with dependencies'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule(new ModuleBuilder('test.nebula:foo:1.0.0').addDependency('test.nebula:bar:1.1.1').build())
                .addModule(new ModuleBuilder('test.nebula:bar:1.1.1').addDependency('test.nebula:baz:23.1.3').build())

        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 3
        def bar = graph.nodes.find { it.artifact == 'bar' }
        bar.dependencies.size() == 1
        def dep = bar.dependencies.find()
        dep.group == 'test.nebula'
        dep.artifact == 'baz'
        dep.version == '23.1.3'
        dep.classifier == null
        dep.extension == null
    }

    def 'add module with dependencies, verify modules are not replaced with placeholder'() {
        def builder = new DependencyGraphBuilder()
        builder.addModule(new ModuleBuilder('test.nebula:bar:1.1.1').addDependency('test.nebula:baz:23.1.3').build())
                .addModule(new ModuleBuilder('test.nebula:foo:1.0.0').addDependency('test.nebula:bar:1.1.1').build())


        when:
        DependencyGraph graph = builder.build()

        then:
        graph.nodes.size() == 3
        def bar = graph.nodes.find { it.artifact == 'bar' }
        bar.dependencies.size() == 1
        def dep = bar.dependencies.find()
        dep.group == 'test.nebula'
        dep.artifact == 'baz'
        dep.version == '23.1.3'
        dep.classifier == null
        dep.extension == null
    }
}
