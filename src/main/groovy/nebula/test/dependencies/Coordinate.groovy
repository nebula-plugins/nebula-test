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

import groovy.transform.Immutable

@Immutable
class Coordinate {
    String group
    String artifact
    String version
    String classifier
    String extension

    @Override
    String toString() {
        def sb = "${group}:${artifact}:${version}"
        if (classifier) sb <<= ":${classifier}"
        if (extension) sb <<= "@${extension}"
        return sb
    }

    public static Coordinate of(String s) {
        def (substr, extension) = s.trim().tokenize("@")

        def (group, artifact, version, classifier) = substr.tokenize(':')
        return new Coordinate(
                group: group, artifact: artifact, version: version, classifier: classifier, extension: extension)
    }
}
