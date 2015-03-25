package nebula.test.multiproject

import groovy.transform.CompileStatic
import nebula.test.IntegrationSpec

@CompileStatic
class MultiProjectIntegrationHelper {
    static String lineEnd = System.getProperty('line.separator')

    File projectDir
    File settingsFile

    MultiProjectIntegrationHelper(File projectDir, File settingsFile) {
        this.projectDir = projectDir
        this.settingsFile = settingsFile
    }

    MultiProjectIntegrationHelper(IntegrationSpec spec) {
        this(spec.projectDir, spec.settingsFile)
    }

    Map<String, MultiProjectIntegrationInfo> create(Collection<String> projectNames) {
        Map<String, MultiProjectIntegrationInfo> info = [:]

        projectNames.each {
            settingsFile << "include '${it}'${lineEnd}"
            def dir = new File(projectDir, it)
            dir.mkdirs()
            def buildFile = new File(dir, 'build.gradle')

            info[it] = new MultiProjectIntegrationInfo(name: it, directory: dir, buildGradle: buildFile)
        }

        info
    }

    File addSubproject(String name) {
        settingsFile << "include '${name}'${lineEnd}"
        def dir = new File(projectDir, name)
        dir.mkdirs()

        dir
    }

    File addSubproject(String name, String gradleContents) {
        def dir = addSubproject(name)
        new File(dir, 'build.gradle').text = gradleContents

        dir
    }
}
