package nebula.test.dependencies

class DependencyGraphBuilder {

    private static final DEFAULT_STATUS = 'integration'

    Map<String, DependencyGraphNode> modules = [:]

    DependencyGraphBuilder addModule(String coordinate) {
        def (group, artifact, version, status) = coordinate.trim().tokenize(':')
        addModule(group, artifact, version, status)
    }

    DependencyGraphBuilder addModule(String group, String artifact, String version) {
        addModule(group, artifact, version, DEFAULT_STATUS)
    }

    DependencyGraphBuilder addModule(String group, String artifact, String version, String status) {
        String key = "${group}:${artifact}:${version}".toString()
        modules[key] = new DependencyGraphNode(coordinate: new Coordinate(group: group, artifact: artifact, version: version), status: status ?: DEFAULT_STATUS)
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
