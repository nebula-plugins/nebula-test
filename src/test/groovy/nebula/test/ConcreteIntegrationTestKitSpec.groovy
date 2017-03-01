/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nebula.test

import org.gradle.testkit.runner.TaskOutcome

class ConcreteIntegrationTestKitSpec extends IntegrationTestKitSpec {
    @Override
    def configurePluginClasspath() {
        // intentionally empty to test basics of runner, as this is not a plugin it doesn't have a plugin classpath
    }

    def "can run build"() {
        given:
        buildFile << """\
            apply plugin: "java"
            """.stripIndent()

        writeHelloWorld("test.nebula")

        when:
        def buildResult = runTasks("assemble")

        then:
        buildResult.task(":compileJava").outcome == TaskOutcome.SUCCESS
    }
}
