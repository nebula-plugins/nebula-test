/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nebula.test.dependencies

import spock.lang.Specification

class DependencyGraphSpec extends Specification {
    def 'one node graph'() {
        when:
        def graph = new DependencyGraph(['test:foo:1.0.0'])

        then:
        graph.nodes.size() == 1
        DependencyGraphNode node = graph.nodes[0]
        node.group == 'test'
        node.artifact == 'foo'
        node.version == '1.0.0'
        node.classifier == null
        node.extension == null
        node.dependencies.size() == 0
        node.toString() == 'test:foo:1.0.0'
    }

    def 'node with dependencies'() {
        when:
        def graph = new DependencyGraph(['test:foo:1.0.0:baz@zip -> test:bar:1.+:bat@jar'])

        then:
        graph.nodes.size() == 1
        DependencyGraphNode node = graph.nodes[0]
        node.group == 'test'
        node.artifact == 'foo'
        node.version == '1.0.0'
        node.classifier == 'baz'
        node.extension == 'zip'
        node.dependencies.size() == 1 
        Coordinate dependency = node.dependencies[0]
        dependency.group == 'test'
        dependency.artifact == 'bar'
        dependency.version == '1.+'
        dependency.classifier == 'bat'
        dependency.extension == 'jar'
    }

    def 'node with multiple dependencies'() {
        when:
        def graph = new DependencyGraph(['test:foo:1.0.0 -> test:bar:1.+|g:a:[1.0.0,2.0.0)|g1:a1:1.1.1'])

        then:
        graph.nodes.size() == 1
        graph.nodes[0].dependencies.size() == 3 
        def dependencies = graph.nodes[0].dependencies.collect { it.toString() }
        dependencies.contains 'test:bar:1.+'
        dependencies.contains 'g:a:[1.0.0,2.0.0)'
        dependencies.contains 'g1:a1:1.1.1'
    }

    def 'check var arg constructor'() {
        when:
        def graph = new DependencyGraph('test:foo:1.0.0', 'test:bar:1.1.1')

        then:
        graph.nodes.size() == 2
    }
}
