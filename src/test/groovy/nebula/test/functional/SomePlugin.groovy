package nebula.test.functional

import nebula.test.functional.foo.Thing
import org.gradle.api.Plugin
import org.gradle.api.Project

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
                project.logger.quiet "I ran!"
            }
        }

        project.task("doIt") {
            onlyIf {
                project.hasProperty('skip') ? !project.properties['skip'].toBoolean() : true
            }
            doLast { project.logger.quiet 'Did it!' }
        }

        project.task("print") {
            doLast {
                println "Printed (stdout)"
                System.err.println 'Printed (stderr)'
            }
        }
    }
}
