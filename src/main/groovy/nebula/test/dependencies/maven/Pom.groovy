/*
 * Copyright 2016 Netflix, Inc.
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
package nebula.test.dependencies.maven

import groovy.xml.MarkupBuilder

class Pom {
    Artifact artifact
    Set<Artifact> dependencies = new TreeSet<>()
    Set<Artifact> dependencyManagementArtifacts = new TreeSet<>()

    Pom(String group, String artifact, String version) {
        this.artifact = new Artifact(group, artifact, version)
    }

    Pom(String group, String artifact, String version, ArtifactType type) {
        this(group, artifact, version)
        this.artifact.type = type
    }

    Pom addDependency(Artifact artifact) {
        dependencies.add(artifact)

        this
    }

    Pom addDependency(String group, String name, String version) {
        dependencies.add(new Artifact(group, name, version))

        this
    }

    Pom addManagementDependency(Artifact artifact) {
        dependencyManagementArtifacts.add(artifact)

        this
    }

    Pom addManagementDependency(String group, String name, String version) {
        dependencyManagementArtifacts.add(new Artifact(group, name, version))

        this
    }

    String getFilename() {
        "${artifact.artifact}-${artifact.version}.pom"
    }

    String generate() {
        def writer = new StringWriter()
        def pom = new MarkupBuilder(writer)
        pom.setDoubleQuotes(true)
        pom.mkp.xmlDeclaration(version: '1.0', encoding: 'UTF-8')
        pom.project('xsi:schemaLocation' : 'http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd', 'xmlns' : 'http://maven.apache.org/POM/4.0.0', 'xmlns:xsi' : 'http://www.w3.org/2001/XMLSchema-instance') {
            modelVersion('4.0.0')
            groupId(artifact.group)
            artifactId(artifact.artifact)
            version(artifact.version)
            if (artifact.type != ArtifactType.JAR) {
                packaging(artifact.type.packaging)
            }
            if (dependencyManagementArtifacts) {
                dependencyManagement {
                    dependencyManagementArtifacts.each { Artifact a ->
                        dependency {
                            groupId(a.group)
                            artifactId(a.artifact)
                            version(a.version)
                        }
                    }
                }
            }
            if (dependencies) {
                dependencies {
                    dependencies.each { Artifact a ->
                        dependency {
                            groupId(a.group)
                            artifactId(a.artifact)
                            version(a.version)
                        }
                    }
                }
            }
        }

        writer.toString()
    }
}
