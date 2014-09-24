package nebula.test.dependencies

class ModuleBuilder {
    Coordinate module
    List<Coordinate> dependencies = []

    ModuleBuilder(String coordinate) {
        def (group, artifact, version) = coordinate.tokenize(':')
        module = new Coordinate(group: group, artifact: artifact, version: version)
    }

    ModuleBuilder(String group, String artifact, String version) {
        module = new Coordinate(group: group, artifact: artifact, version: version)
    }

    ModuleBuilder addDependency(String dependency) {
        def (group, artifact, version) = dependency.tokenize(':')
        dependencies << new Coordinate(group: group, artifact: artifact, version: version)

        this
    }

    ModuleBuilder addDependency(String group, String artifact, String version) {
        dependencies << new Coordinate(group: group, artifact: artifact, version: version)

        this
    }

    DependencyGraphNode build() {
        new DependencyGraphNode(coordinate: module, dependencies: dependencies)
    }
}
