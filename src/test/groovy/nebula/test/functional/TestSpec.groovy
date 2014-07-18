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

    def "Check up-to-date and skipped task states"() {
        given:
        tmp.newFile("build.gradle") << """
            apply plugin: ${SomePlugin.name}
        """

        when:
        ExecutionResult result = runner.run(tmp.root, ["echo", "doIt", "-PupToDate=false", "-Pskip=false"])
        !result.wasExecuted(":hush")
        result.wasExecuted(":echo")
        !result.wasUpToDate(":echo")
        result.wasExecuted(":doIt")
        !result.wasSkipped(":doIt")

        then:
        result.standardOutput.contains("I ran!")
        result.standardOutput.contains("Did it!")

        when:
        result = runner.run(tmp.root, ["echo", "doIt", "-PupToDate=true", "-Pskip=true"])

        then:
        !result.standardOutput.contains("I ran!")
        !result.standardOutput.contains("Did it!")
        result.wasExecuted(":echo")
        result.wasUpToDate(":echo")
        result.wasExecuted(":doIt")
        result.wasSkipped(":doIt")
    }

    def "Task path doesn't need to start with colon"() {
        given:
        tmp.newFile("build.gradle") << """
            apply plugin: ${SomePlugin.name}
        """

        when:
        ExecutionResult result = runner.run(tmp.root, ["echo", "doIt"])
        result.wasExecuted("echo")
        !result.wasUpToDate("echo")
        !result.wasSkipped("doIt")

        then:
        result.standardOutput.contains("I ran!")
        result.standardOutput.contains("Did it!")
    }
}

class SomePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("echo") {
            outputs.upToDateWhen {
                project.hasProperty('upToDate') ? project.properties['upToDate'].toBoolean() : false
            }

            doLast {
                new Thing() // Class in another package
                spock.lang.Specification // is a compile dependency, test it's available
                println "I ran!"
            }
        }

        project.task("doIt") {
            onlyIf {
                project.hasProperty('skip') ? !project.properties['skip'].toBoolean() : true
            }
            doLast { println 'Did it!' }
        }
    }
}
