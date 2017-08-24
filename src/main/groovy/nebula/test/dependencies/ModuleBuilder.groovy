package nebula.test.dependencies

class ModuleBuilder {
    Coordinate module
    List<Coordinate> dependencies = []

    ModuleBuilder(String coordinate) {
        module = Coordinate.of(coordinate)
    }

    ModuleBuilder(String group, String artifact, String version) {
        module = new Coordinate(group: group, artifact: artifact, version: version)
    }

    ModuleBuilder(String group, String artifact, String version, String classifier, String extension) {
        module = new Coordinate(
                group: group, artifact: artifact, version: version, classifier: classifier, extension: extension)
    }

    ModuleBuilder addDependency(String dependency) {
        dependencies << Coordinate.of(dependency)

        this
    }

    ModuleBuilder addDependency(String group, String artifact, String version) {
        dependencies <<  new Coordinate(
                group: group, artifact: artifact, version: version)

        this
    }

    ModuleBuilder addDependency(String group, String artifact, String version, String classifier, String extension) {
        dependencies <<  new Coordinate(
                group: group, artifact: artifact, version: version, classifier: classifier, extension: extension)

        this
    }

    DependencyGraphNode build() {
        new DependencyGraphNode(coordinate: module, dependencies: dependencies)
    }
}
