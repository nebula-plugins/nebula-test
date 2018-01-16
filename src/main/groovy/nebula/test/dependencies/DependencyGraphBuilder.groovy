package nebula.test.dependencies

class DependencyGraphBuilder {
    Map<String, DependencyGraphNode> modules = [:]

    DependencyGraphBuilder addModule(String coordinate) {
        def (group, artifact, version) = coordinate.trim().tokenize(':')
        addModule(group, artifact, version)
    }

    DependencyGraphBuilder addModule(String group, String artifact, String version) {
        String key = "${group}:${artifact}:${version}".toString()
        modules[key] = new DependencyGraphNode(coordinate: new Coordinate(group: group, artifact: artifact, version: version))

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
