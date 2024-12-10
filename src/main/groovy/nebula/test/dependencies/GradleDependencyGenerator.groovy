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

import nebula.test.functional.GradleRunnerFactory
import nebula.test.gradle.GradleVersionComparator
import org.gradle.api.invocation.Gradle

class GradleDependencyGenerator {
    private static final String DEFAULT_GRADLE_VERSION = '5.2.1'
    private static final String GRADLE_FIVE_ZERO = '5.0.0'
    private static final String LEGACY_PATTERN_LAYOUT = "layout('pattern')"
    private static final String PATTERN_LAYOUT = "patternLayout"

    static final String STANDARD_SUBPROJECT_BLOCK = '''\
        subprojects {
            apply plugin: 'maven-publish'
            apply plugin: 'ivy-publish'
            apply plugin: 'java-library'

            publishing {
                repositories {
                    maven {
                        url = "../mavenrepo"
                    }
                    ivy {
                        url = "../ivyrepo"
                        patternLayout {
                            ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
                            artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
                            m2compatible = true
                        }
                    }
                }
            }
        }
    '''.stripIndent()

    static final String LEGACY_STANDARD_SUBPROJECT_BLOCK = '''\
        subprojects {
            apply plugin: 'maven-publish'
            apply plugin: 'ivy-publish'
            apply plugin: 'java-library'

            publishing {
                repositories {
                    maven {
                        url = "../mavenrepo"
                    }
                    ivy {
                        url = "../ivyrepo"
                        layout('pattern') {
                            ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
                            artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
                            m2compatible = true
                        }
                    }
                }
            }
        }
    '''.stripIndent()

    static final String BUILD_GRADLE = 'build.gradle'

    private boolean generated = false

    DependencyGraph graph
    File gradleRoot
    File ivyRepoDir
    File mavenRepoDir
    String gradleVersion

    GradleDependencyGenerator(String gradleVersion, DependencyGraph graph, String directory = 'build/testrepogen') {
        this.graph = graph
        this.gradleRoot = new File(directory)
        this.ivyRepoDir = new File(directory, 'ivyrepo')
        this.mavenRepoDir = new File(directory, 'mavenrepo')
        this.gradleVersion = gradleVersion
        generateGradleFiles(gradleVersion)
    }

    GradleDependencyGenerator(Gradle gradle, DependencyGraph graph, String directory = 'build/testrepogen') {
        this(gradle.gradleVersion, graph, directory)
    }

    GradleDependencyGenerator(DependencyGraph graph, String directory = 'build/testrepogen') {
        this(DEFAULT_GRADLE_VERSION, graph, directory)
    }

    File generateTestMavenRepo() {
        runTasks('publishMavenPublicationToMavenRepository')

        mavenRepoDir
    }

    String getMavenRepoDirPath() {
        mavenRepoDir.absolutePath
    }

    String getMavenRepoUrl() {
        mavenRepoDir.toURI().toURL()
    }

    String getMavenRepositoryBlock() {
        """\
            maven { url = '${getMavenRepoUrl()}' }
        """.stripIndent()
    }

    File generateTestIvyRepo() {
        runTasks('publishIvyPublicationToIvyRepository')

        ivyRepoDir
    }

    String getIvyRepoDirPath() {
        ivyRepoDir.absolutePath
    }

    String getIvyRepoUrl() {
        ivyRepoDir.toURI().toURL()
    }

    String getIvyRepositoryBlock() {
        use(GradleVersionComparator) {
            boolean isGradleOlderThanGradleFive = gradleVersion.versionLessThan(GRADLE_FIVE_ZERO)
            String layoutPattern = isGradleOlderThanGradleFive ? LEGACY_PATTERN_LAYOUT : PATTERN_LAYOUT
            return """\
            ivy {
                url = '${getIvyRepoUrl()}'
                ${layoutPattern} {
                    ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
                    artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
                    m2compatible = true
                }
            }
        """.stripIndent()
        }
    }

    private void generateGradleFiles(String gradleVersion) {
        use(GradleVersionComparator) {
            if (generated) {
                return
            } else {
                generated = true
            }

            gradleRoot.mkdirs()
            def rootBuildGradle = new File(gradleRoot, BUILD_GRADLE)
            rootBuildGradle.text = gradleVersion.versionLessThan(GRADLE_FIVE_ZERO) ? LEGACY_STANDARD_SUBPROJECT_BLOCK : STANDARD_SUBPROJECT_BLOCK
            def includes = []
            graph.nodes.each { DependencyGraphNode n ->
                String subName = "${n.group}.${n.artifact}_${n.version.replaceAll(/\./, '_')}"
                includes << subName
                def subfolder = new File(gradleRoot, subName)
                subfolder.mkdir()
                def subBuildGradle = new File(subfolder, BUILD_GRADLE)
                subBuildGradle.text = generateSubBuildGradle(n)
            }
            def settingsGradle = new File(gradleRoot, 'settings.gradle')
            settingsGradle.text = 'include ' + includes.collect { "'${it}'" }.join(', ')
        }
    }

    private String generateSubBuildGradle(DependencyGraphNode node) {

        StringWriter block = new StringWriter()
        if (node.dependencies) {
            block.withPrintWriter { writer ->
                writer.println 'dependencies {'
                node.dependencies.each { writer.println "    api '${it}'" }
                writer.println '}'
            }
        }

        """\
            group = '${node.group}'
            version = '${node.version}'
            ext {
                artifactName = '${node.artifact}'
            }
            
            targetCompatibility = ${node.targetCompatibility}
            
            publishing {
                publications {
                    maven(MavenPublication) {
                        artifactId artifactName
                        from components.java
                    }
                    ivy(IvyPublication) {
                        module artifactName
                        from components.java
                        descriptor.status = '${node.status}'
                    }
                }
            }
        """.stripIndent() + block.toString()
    }

    private void runTasks(String tasks) {
        def runner = GradleRunnerFactory.createTooling() // Could optionally use Launcher
        runner.run(gradleRoot, tasks.tokenize()).rethrowFailure()
    }
}
