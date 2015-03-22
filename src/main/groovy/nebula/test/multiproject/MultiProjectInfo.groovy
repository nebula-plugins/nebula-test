package nebula.test.multiproject

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class MultiProjectInfo {
    String name
    Project parent
    Project project
    File directory
}
