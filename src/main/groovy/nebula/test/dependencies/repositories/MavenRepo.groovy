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
package nebula.test.dependencies.repositories

import groovy.xml.MarkupBuilder
import nebula.test.dependencies.maven.Pom

import java.text.SimpleDateFormat

class MavenRepo {
    Set<Pom> poms = new HashSet<>()
    File root

    String repoString() {
        """\
            maven { url '${root.absolutePath}' }
            """.stripIndent()
    }

    void generate() {
        if (!root.exists()) {
            root.mkdirs()
        }
        poms.each { Pom pom ->
            def path = "${groupAndArtifactPath(pom)}/${pom.artifact.version}"
            def dir = new File(root, path)
            dir.mkdirs()
            new File(dir, pom.filename).text = pom.generate()
        }
        generateMavenMetadata(poms)
    }

    private void generateMavenMetadata(Set<Pom> poms) {
        Map<String, List<Pom>> groupedPoms = poms.groupBy { groupAndArtifactPath(it) }
        groupedPoms.each { groupAndArtifactPath, List<Pom> pomGroup ->
            List<Pom> sortedPoms = pomGroup.sort { it.artifact.version }
            File metadataFile = new File(root, "$groupAndArtifactPath/maven-metadata.xml")
            def writer = new FileWriter(metadataFile)
            def xml = new MarkupBuilder(writer)
            xml.metadata {
                groupId(sortedPoms.first().artifact.group)
                artifactId(sortedPoms.first().artifact.artifact)
                versioning {
                    latest(sortedPoms.last().artifact.version)
                    release(sortedPoms.last().artifact.version)
                    versions {
                        sortedPoms.each {
                            version(it.artifact.version)
                        }
                    }
                }
                lastUpdated(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()))
            }
        }
    }

    private static String groupAndArtifactPath(Pom pom) {
        "${pom.artifact.group.replaceAll(/\./, '/')}/${pom.artifact.artifact}"
    }
}
