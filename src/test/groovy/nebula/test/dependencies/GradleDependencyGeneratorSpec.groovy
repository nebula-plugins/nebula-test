/*
 * Copyright 2014-2017 Netflix, Inc.
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

import org.gradle.api.JavaVersion
import org.gradle.api.invocation.Gradle
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

    def 'generate a maven repo with a SNAPSHOT'() {
        def directory = 'build/testdependencies/testmavenreposnapshot'
        def graph = ['test.maven:foo:1.0.1-SNAPSHOT']
        def generator = new GradleDependencyGenerator(new DependencyGraph(graph), directory)

        when:
        generator.generateTestMavenRepo()

        then:
        def mavenRepo = new File(directory + '/mavenrepo')
        new File(mavenRepo, 'test/maven/foo/1.0.1-SNAPSHOT/').listFiles().find {
            it.name =~ /foo-1\.0\.1-\d{8}\.\d{6}-\d?\.pom/
        }
    }

    def 'publishes ivy status using DependencyGraphBuilder.addModule - passing status'() {
        given:
        def directory = 'build/testdependencies/ivypublishAddModuleWithStatus'
        def graph = new DependencyGraphBuilder().addModule('test.resolved:a:1.0.0:release')
                .addModule('test.resolved:a:1.1.0:release').build()
        def generator = new GradleDependencyGenerator(graph, directory)

        when:
        generator.generateTestIvyRepo()

        then:
        def ivyRepo = new File('build/testdependencies/ivypublishAddModuleWithStatus')
        def xml = new File(ivyRepo, 'ivyrepo/test/resolved/a/1.1.0/a-1.1.0-ivy.xml')
        xml.exists()
        xml.text.contains("status=\"release\"")
    }

    def 'publishes ivy status using DependencyGraphBuilder.addModule'() {
        given:
        def directory = 'build/testdependencies/ivypublishAddModule'
        def graph = new DependencyGraphBuilder().addModule('test.resolved:a:1.0.0')
                .addModule('test.resolved:a:1.1.0').build()
        def generator = new GradleDependencyGenerator(graph, directory)

        when:
        generator.generateTestIvyRepo()

        then:
        def ivyRepo = new File('build/testdependencies/ivypublishAddModule')
        def xml = new File(ivyRepo, 'ivyrepo/test/resolved/a/1.1.0/a-1.1.0-ivy.xml')
        xml.exists()
        xml.text.contains("status=\"integration\"")
    }

    def 'DependencyGraphBuilder using map for DependencyGraph'() {
        given:
        def directory = 'build/testdependencies/ivypublishMap'
        def graph = ['test.resolved:a:1.0.0', 'test.resolved:a:1.1.0']
        def generator = new GradleDependencyGenerator(new DependencyGraph(graph), directory)

        when:
        generator.generateTestIvyRepo()

        then:
        def ivyRepo = new File('build/testdependencies/ivypublishMap')
        def xml = new File(ivyRepo, 'ivyrepo/test/resolved/a/1.1.0/a-1.1.0-ivy.xml')
        xml.exists()
        xml.text.contains("status=\"integration\"")
    }

    def 'generate an ivy repo'() {
        def directory = 'build/testdependencies/testivyrepo'
        def graph = ['test.ivy:foo:1.0.0']
        def generator = new GradleDependencyGenerator(new DependencyGraph(graph), directory)

        when:
        generator.generateTestIvyRepo()

        then:
        def ivyRepo = new File(directory + '/ivyrepo')
        new File(ivyRepo, 'test/ivy/foo/1.0.0/foo-1.0.0-ivy.xml').exists()
        new File(ivyRepo, 'test/ivy/foo/1.0.0/foo-1.0.0.jar').exists()
    }

    def 'generate an ivy repo block - gradle version older than 5.x'() {
        def directory = 'build/testdependencies/testivyrepo'
        def graph = ['test.ivy:foo:1.0.0']
        def generator = new GradleDependencyGenerator("4.10.3", new DependencyGraph(graph), directory)

        when:
        String block = generator.getIvyRepositoryBlock()

        then:
        block.contains("""
    layout('pattern') {
        ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
        artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
        m2compatible = true
    }
""")
    }

    def 'generate an ivy repo block - uses Gradle object newer versions of Gradle'() {
        Gradle gradleMock = Mock(Gradle)
        def directory = 'build/testdependencies/testivyrepo'
        def graph = ['test.ivy:foo:1.0.0']

        when:
        def generator = new GradleDependencyGenerator(gradleMock, new DependencyGraph(graph), directory)
        String block = generator.getIvyRepositoryBlock()

        then:
        1 * gradleMock.getGradleVersion() >> "5.2.1"

        block.contains("""
    patternLayout {
        ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
        artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
        m2compatible = true
    }
""")

    }

    def 'generate an ivy repo block - uses Gradle object old versions of Gradle'() {
        Gradle gradleMock = Mock(Gradle)
        def directory = 'build/testdependencies/testivyrepo'
        def graph = ['test.ivy:foo:1.0.0']

        when:
        def generator = new GradleDependencyGenerator(gradleMock, new DependencyGraph(graph), directory)
        String block = generator.getIvyRepositoryBlock()

        then:
        1 * gradleMock.getGradleVersion() >> "4.10.3"

        block.contains("""
    layout('pattern') {
        ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
        artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
        m2compatible = true
    }
""")
    }

    def 'generate an ivy repo block - gradle version newer than 5.x'() {
        def directory = 'build/testdependencies/testivyrepo'
        def graph = ['test.ivy:foo:1.0.0']
        def generator = new GradleDependencyGenerator("5.2.1", new DependencyGraph(graph), directory)

        when:
        String block = generator.getIvyRepositoryBlock()

        then:
        block.contains("""
    patternLayout {
        ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
        artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
        m2compatible = true
    }
""")
    }

    def 'check ivy status'() {
        def directory = 'build/testdependencies/ivyxml'
        def graph = ['test.ivy:foo:1.0.0']
        def generator = new GradleDependencyGenerator(new DependencyGraph(graph), directory)

        when:
        generator.generateTestIvyRepo()

        then:
        def repo = new File(directory)
        new File(repo, 'ivyrepo/test/ivy/foo/1.0.0/foo-1.0.0-ivy.xml').text.contains 'status="integration"'
    }

    def 'allow different ivy status'() {
        def directory = 'build/testdependencies/ivyxml'
        def graph = [
                new DependencyGraphNode(coordinate: new Coordinate(group: 'test.ivy', artifact: 'foo-final', version: '1.0.0'), status: "release"),
                new DependencyGraphNode(coordinate: new Coordinate(group: 'test.ivy', artifact: 'foo-candidate', version: '1.0.0'), status: "candidate")
        ]
        def generator = new GradleDependencyGenerator(new DependencyGraph(nodes: graph), directory)

        when:
        generator.generateTestIvyRepo()

        then:
        def repo = new File(directory)
        new File(repo, 'ivyrepo/test/ivy/foo-final/1.0.0/foo-final-1.0.0-ivy.xml').text.contains 'status="release"'
        new File(repo, 'ivyrepo/test/ivy/foo-candidate/1.0.0/foo-candidate-1.0.0-ivy.xml').text.contains 'status="candidate"'
    }

    def 'allow different target compatibility'() {
        def directory = 'build/testdependencies/testmavenrepo'
        def graph = [
                new DependencyGraphNode(coordinate: new Coordinate(group: 'test.maven', artifact: 'foo-final', version: '1.0.0'), status: "release", targetCompatibility: 17),
        ]
        def generator = new GradleDependencyGenerator(new DependencyGraph(nodes: graph), directory)

        when:
        generator.generateTestMavenRepo()

        then:
        def repo = new File(directory)
        new File(repo, 'mavenrepo/test/maven/foo-final/1.0.0/foo-final-1.0.0.module').text.contains '"org.gradle.jvm.version": 17'
    }

    def 'check ivy xml'() {
        def directory = 'build/testdependencies/ivyxml'
        def graph = ['test.ivy:foo:1.0.0 -> test.ivy:bar:1.1.0']
        def generator = new GradleDependencyGenerator(new DependencyGraph(graph), directory)

        when:
        generator.generateTestIvyRepo()

        then:
        def repo = new File(directory)
        new File(repo, 'test.ivy.foo_1_0_0/build.gradle').text.contains 'api \'test.ivy:bar:1.1.0\''
        new File(repo, 'ivyrepo/test/ivy/foo/1.0.0/foo-1.0.0-ivy.xml').text.contains '<dependency org="test.ivy" name="bar" rev="1.1.0" conf="compile-&gt;default"/>'
    }

    def 'check maven pom'() {
        def directory = 'build/testdependencies/mavenpom'
        def graph = ['test.maven:foo:1.0.0 -> test.maven:bar:1.+']
        def generator = new GradleDependencyGenerator(new DependencyGraph(graph), directory)

        when:
        generator.generateTestMavenRepo()

        then:
        def repo = new File(directory)
        new File(repo, 'test.maven.foo_1_0_0/build.gradle').text.contains 'api \'test.maven:bar:1.+\''
        def pom = new File(repo, 'mavenrepo/test/maven/foo/1.0.0/foo-1.0.0.pom').text
        pom.contains '<groupId>test.maven</groupId>'
        pom.contains '<artifactId>bar</artifactId>'
        pom.contains '<version>1.+</version>'
        pom.contains '<scope>compile</scope>'
    }

    def 'multiple libraries with dependencies'() {
        def graph = ['integration.test:foo:1.0.0', 
                'integration.test:foo:1.0.1',
                'integration.test:bar:1.0.0 -> integration.test:foo:1.+',
                'integration.test:baz:0.9.0 -> integration.test:foo:[1.0.0,2.0.0)|integration.test:bar:1.0.+']

        def generator = new GradleDependencyGenerator(new DependencyGraph(graph))

        when:
        generator.generateTestMavenRepo()
        generator.generateTestIvyRepo()

        then:
        def mavenRepo = new File('build/testrepogen/mavenrepo')
        def group = 'integration.test'
        mavenFilesExist(group, 'foo', '1.0.0', mavenRepo)
        mavenFilesExist(group, 'foo', '1.0.1', mavenRepo)
        mavenFilesExist(group, 'bar', '1.0.0', mavenRepo)
        mavenFilesExist(group, 'baz', '0.9.0', mavenRepo)
        def ivyRepo = new File('build/testrepogen/ivyrepo')
        ivyFilesExist(group, 'foo', '1.0.0', ivyRepo)
        ivyFilesExist(group, 'foo', '1.0.1', ivyRepo)
        ivyFilesExist(group, 'bar', '1.0.0', ivyRepo)
        ivyFilesExist(group, 'baz', '0.9.0', ivyRepo)
    }

    def 'generator returns location of the ivy repository'() {
        def generator = new GradleDependencyGenerator(new DependencyGraph(['test.ivy:foo:1.0.0']))

        when:
        File dir = generator.generateTestIvyRepo()

        then:
        dir == new File('build/testrepogen/ivyrepo')
    }

    def 'ask generator for location of the ivy repository'() {
        def generator = new GradleDependencyGenerator(new DependencyGraph(['test.ivy:foo:1.0.0']), 'build/test')

        when:
        File dir = generator.ivyRepoDir

        then:
        dir == new File('build/test/ivyrepo')
    }

    def 'ask generator for string location of the ivy repository'() {
        def generator = new GradleDependencyGenerator(new DependencyGraph(['test.ivy:foo:1.0.0']), 'build/test')

        when:
        String name = generator.ivyRepoDirPath

        then:
        name == new File('build/test/ivyrepo').absolutePath
    }

    def 'integration spec ivy repository block is available'() {
        def generator = new GradleDependencyGenerator(new DependencyGraph(['test.ivy:foo:1.0.0']), 'build/test')
        String expectedBlock = """\
            ivy {
                url = '${generator.getIvyRepoUrl()}'
                patternLayout {
                    ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
                    artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
                    m2compatible = true
                }
            }
        """.stripIndent()

        when:
        String block = generator.ivyRepositoryBlock

        then:
        block == expectedBlock
    }

    def 'generator returns location of the maven repository'() {
        def generator = new GradleDependencyGenerator(new DependencyGraph(['test.maven:foo:1.0.0']))

        when:
        File dir = generator.generateTestMavenRepo()

        then:
        dir == new File('build/testrepogen/mavenrepo')
    }

    def 'ask generator for location of the maven repository'() {
        def generator = new GradleDependencyGenerator(new DependencyGraph(['test.maven:foo:1.0.0']), 'build/test')

        when:
        File dir = generator.mavenRepoDir

        then:
        dir == new File('build/test/mavenrepo')
    }

    def 'ask generator for string location of the maven repository'() {
        def generator = new GradleDependencyGenerator(new DependencyGraph(['test.maven:foo:1.0.0']), 'build/testmaven')

        when:
        String name = generator.mavenRepoDirPath

        then:
        name == new File('build/testmaven/mavenrepo').absolutePath
    }

    def 'integration spec maven repository block is available'() {
        def generator = new GradleDependencyGenerator(new DependencyGraph(['test.maven:foo:1.0.0']), 'build/test')
        String expectedBlock = """\
            maven { url = '${generator.getMavenRepoUrl()}' }
        """.stripIndent()

        when:
        String block = generator.mavenRepositoryBlock

        then:
        block == expectedBlock
    }

    private Boolean mavenFilesExist(String group, String artifact, String version, File repository) {
        String baseName = artifactPath(group, artifact, version)
        Boolean pomExists = new File(repository, "${baseName}.pom").exists()
        Boolean jarExists = new File(repository, "${baseName}.jar").exists()

        pomExists && jarExists
    }

    private Boolean ivyFilesExist(String group, String artifact, String version, File repository) {
        String baseName = artifactPath(group, artifact, version)
        Boolean ivyExists = new File(repository, "${baseName}-ivy.xml").exists()
        Boolean jarExists = new File(repository, "${baseName}.jar").exists()

        ivyExists && jarExists
    }

    String artifactPath(String group, String artifact, String version) {
        "${group.replaceAll(/\./, '/')}/${artifact}/${version}/${artifact}-${version}"    
    }
}
