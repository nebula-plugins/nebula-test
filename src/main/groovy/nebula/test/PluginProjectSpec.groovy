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
package nebula.test

import org.gradle.testfixtures.ProjectBuilder

/**
 * Create some basic tests that all plugins should pass
 */
abstract class PluginProjectSpec extends ProjectSpec {
    abstract String getPluginName()

    def 'apply does not throw exceptions'() {
        when:
        project.apply plugin: pluginName

        then:
        noExceptionThrown()
    }

    def 'apply is idempotent'() {
        when:
        project.apply plugin: pluginName
        project.apply plugin: pluginName

        then:
        noExceptionThrown()
    }

    def 'apply is fine on all levels of multiproject'() {
        def sub = ProjectBuilder.builder().withName('sub').withProjectDir(new File(projectDir, 'sub')).withParent(project).build()
        project.subprojects.add(sub)

        when:
        project.apply plugin: pluginName
        sub.apply plugin: pluginName

        then:
        noExceptionThrown()
    }
}
