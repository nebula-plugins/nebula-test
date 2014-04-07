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

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection

class GradleDependencyProject {
    static final String STANDARD_SUBPROJECT_BLOCK = '''\
        subprojects {
            apply plugin: 'maven-publish'
            apply plugin: 'ivy-publish'
            apply plugin: 'java'

            publishing {
                repositories {
                    maven {
                        url "${rootProject.buildDir}/mavenrepo"
                    }
                    ivy {
                        url "${rootProject.buildDir}/ivyrepo"
                        layout('pattern') {
                            ivy '[organisation]/[module]/[revision]/[module]-[revision]-ivy.[ext]'
                            artifact '[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]'
                            m2compatible = true
                        }
                    }
                }
                publications {
                    maven(MavenPublication) {
                        //groupId {  }
                        artifactId { artifactName }
                        //version {  }

                        from components.java
                    }
                    ivy(IvyPublication) {
                        //organisation {  }
                        module { artifactName }
                        //revision {  }

                        from components.java
                    }
                }
            }
        }
    '''.stripIndent()
    static final String BUILD_GRADLE = 'build.gradle'

    DependencyGraph graph
    File gradleRoot

    GradleDependencyProject(DependencyGraph graph) {
        this.graph = graph
        this.gradleRoot = new File('build/testrepogen')
        generateGradleFiles()
    }

    void generateTestMavenRepo() {
        runTasks('publishMavenPublicationToMavenRepository')    
    }

    void generateTestIvyRepo() {
        runTasks('publishIvyPublicationToIvyRepository')
    }

    private void generateGradleFiles() {
        def rootBuildGradle = new File(gradleRoot, BUILD_GRADLE)
        rootBuildGradle.text = STANDARD_SUBPROJECT_BLOCK
        def includes = []
        graph.nodes.each { DependencyGraphNode n ->
            String subName = "${n.group}${n.artifact}_${n.version.replaceAll('.', '_')}"
            includes << subName
            def subfolder = new File(gradleRoot, subName)
            def subBuildGradle = new File(subfolder, BUILD_GRADLE)
            subBuildGradle.text = subBuildGradle(n)
        }
        def settingsGradle = new File(gradleRoot, 'settings.gradle')
        settingsGradle.text = 'include ' + includes.collect { "'${it}'"}.join(', ')
    }

    private String subBuildGradle(DependencyGraphNode node) {
        String dependencies = ''
        if (node.dependencies) {
            dependencies + 'dependencies {'
            node.dependencies.each { dependencies + "    compile '${it}'"}
            dependencies + '}'
        }

        """\
            group = '${node.group}'
            version = '${node.version}'
            ext {
                artifactName = '${node.artifact}'
            }

            ${dependencies}
        """.stripIndent()
    }

    private void runTasks(String tasks) {
        ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(gradleRoot).connect()

        try {
            connection.newBuild().forTasks(tasks).run()
        } finally {
            connection.close()
        }
    }
}
