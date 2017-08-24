package nebula.test.dependencies

class DependencyGraphBuilder {
    Map<String, DependencyGraphNode> modules = [:]

    DependencyGraphBuilder addModule(String coordinateString) {
        Coordinate coordinate = Coordinate.of(coordinateString)
        modules[coordinate.toString()] = new DependencyGraphNode(coordinate: coordinate)

        this
    }

    DependencyGraphBuilder addModule(String group, String artifact, String version) {
        Coordinate coordinate = new Coordinate(group: group, artifact: artifact, version: version);
        modules[coordinate.toString()] = new DependencyGraphNode(coordinate: coordinate)

        this
    }

    DependencyGraphBuilder addModule(String group, String artifact, String version, String classifier, String extension) {
        Coordinate coordinate = new Coordinate(
                group: group, artifact: artifact, version: version, classifier: classifier, extension: extension);
        modules[coordinate.toString()] = new DependencyGraphNode(coordinate: coordinate)

        this
    }

    DependencyGraphBuilder addModule(DependencyGraphNode node) {
        modules[node.coordinate.toString()] = node

        node.dependencies.each { Coordinate dep ->
            if (!modules.containsKey(dep.toString())) {
                modules[dep.toString()] = new DependencyGraphNode(coordinate: dep)
            }
        }

        this
    }

    DependencyGraph build() {
        new DependencyGraph(nodes: modules.values())
    }
}
