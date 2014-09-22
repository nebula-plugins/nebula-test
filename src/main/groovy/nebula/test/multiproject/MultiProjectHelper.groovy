package nebula.test.multiproject

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class MultiProjectHelper {
    Project parent

    MultiProjectHelper(Project parent) {
        this.parent = parent
    }

    Map<String, MultiProjectInfo> create(Collection<String> projectNames) {
        Map<String, MultiProjectInfo> info = [:]

        projectNames.each {
            def subproject = ProjectBuilder.builder().withName(it).withParent(parent).build()
            info[it] = new MultiProjectInfo(name: it, project: subproject, parent: parent)
        }

        info
    }

    Map<String, MultiProjectInfo> createWithDirectories(Collection<String> projectNames) {
        Map<String, MultiProjectInfo> info = [:]

        projectNames.each {
            def subDirectory = new File(parent.projectDir, it)
            subDirectory.mkdirs()
            def subproject = ProjectBuilder.builder().withName(it).withProjectDir(subDirectory).withParent(parent).build()
            info[it] = new MultiProjectInfo(name: it, project: subproject, parent: parent, directory: subDirectory)
        }

        info
    }

    Project addSubproject(String name) {
        ProjectBuilder.builder().withName(name).withParent(parent).build()
    }

    Project addSubprojectWithDirectory(String name) {
        def dir = new File(parent.projectDir, name)
        dir.mkdirs()
        ProjectBuilder.builder().withName(name).withProjectDir(dir).withParent(parent).build()
    }
}
