package nebula.test.multiproject

import nebula.test.ProjectSpec
import org.gradle.api.Project

class MultiProjectHelperSpec extends ProjectSpec {
    def 'create single subproject in multiproject'() {
        when:
        Map<String, MultiProjectInfo> info = helper.create(['sub'])

        then:
        info['sub'].project.parent == project
        info['sub'].directory == null
        project.subprojects.find { it.name == 'sub' } != null
    }

    def 'create single subproject with directory in multiproject'() {
        when:
        Map<String, MultiProjectInfo> info = helper.createWithDirectories(['sub'])

        then:
        info['sub'].directory == new File(project.projectDir, 'sub')
    }

    def 'create multiple subproject in multiproject'() {
        when:
        Map<String, MultiProjectInfo> info = helper.create(['sub1', 'sub2'])

        then:
        info['sub1'].project.parent == project
        info['sub1'].directory == null
        project.subprojects.find { it.name == 'sub1' } != null
        info['sub2'].project.parent == project
        info['sub2'].directory == null
        project.subprojects.find { it.name == 'sub2' } != null
    }

    def 'add a subproject'() {
        when:
        Project sub = addSubproject('sub')

        then:
        sub.parent == project
        project.subprojects.find { it == sub } != null
    }

    def 'add a subproject with directory'() {
        when:
        Project sub = addSubprojectWithDirectory('sub')

        then:
        sub.parent == project
        project.subprojects.find { it == sub } != null
        sub.projectDir == new File(projectDir, 'sub')
    }
}
