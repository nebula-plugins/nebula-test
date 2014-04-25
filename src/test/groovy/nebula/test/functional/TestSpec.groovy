/*
 * Copyright 2012 the original author or authors.
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

package nebula.test.functional

import nebula.test.functional.foo.Thing
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class TestSpec extends Specification {

    @Rule TemporaryFolder tmp
    def runner = GradleRunnerFactory.createTooling()

    def "test thing"() {
        given:
        tmp.newFile("build.gradle") << """
            apply plugin: ${SomePlugin.name}
            echo.outputs.upToDateWhen { true }
        """

        when:
        ExecutionResult result = runner.run(tmp.root, ["echo"])
        !result.wasExecuted(":hush")
        result.wasExecuted(":echo")
        result.wasUpToDate(":echo")

        then:
        result.standardOutput.contains("I ran!")

        when:
        ExecutionResult result2 = runner.run(tmp.root, ["echo"])

        then:
        !result2.standardOutput.contains("I ran!")
        result.wasExecuted(":echo")
        !result.wasUpToDate(":echo")

    }
}

class SomePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("echo") {
            doLast {
                new Thing() // Class in another package
                spock.lang.Specification // is a compile dependency, test it's available
                println "I ran!"
            }
        }
    }
}
