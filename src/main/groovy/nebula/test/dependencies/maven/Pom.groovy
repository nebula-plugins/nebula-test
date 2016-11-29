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

class Pom {
    String group
    String artifact
    String version

    Set<Artifact> dependencies
    Set<Artifact> dependencyManagementArtifacts

    Pom addDependency(Artifact artifact) {
        dependencies.add(artifact)
    }

    Pom addDependency(String group, String name, String version) {
        dependencies.add(new Artifact(group, name, version))
    }

    Pom addManagementDependency(Artifact artifact) {
        dependencyManagementArtifacts.add(artifact)
    }

    Pom addManagementDependency(String group, String name, String version) {
        dependencyManagementArtifacts.add(new Artifact(group, name, version))
    }

    String generate() {

    }
}
