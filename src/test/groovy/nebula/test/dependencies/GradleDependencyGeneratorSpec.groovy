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

class GradleDependencyGeneratorSpec extends Specification {
    def 'generate a maven repo'() {
        def directory = 'build/testdependencies/testmavenrepo'
        def graph = ['test.maven:foo:1.0.0']
        def generator = new GradleDependencyGenerator(new DependencyGraph(graph), directory)

        when:
        generator.generateTestMavenRepo()

        then:
        def mavenRepo = new File(directory + '/mavenrepo')
        new File(mavenRepo, 'test/maven/foo/1.0.0/foo-1.0.0.pom').exists()
        new File(mavenRepo, 'test/maven/foo/1.0.0/foo-1.0.0.jar').exists()
    }

    def 'generate an ivy repo'() {
        def directory = 'build/testdependencies/testivyrepo'
        def graph = ['test.ivy:foo:1.0.0']
        def generator = new GradleDependencyGenerator(new DependencyGraph(graph), directory)

        when:
        generator.generateTestIvyRepo()

        then:
        def mavenRepo = new File(directory + '/ivyrepo')
        new File(mavenRepo, 'test/ivy/foo/1.0.0/foo-1.0.0-ivy.xml').exists()
        new File(mavenRepo, 'test/ivy/foo/1.0.0/foo-1.0.0.jar').exists()
    }
}
