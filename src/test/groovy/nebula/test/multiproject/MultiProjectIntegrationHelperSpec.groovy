package nebula.test.multiproject

import nebula.test.IntegrationSpec

class MultiProjectIntegrationHelperSpec extends IntegrationSpec {
    def 'create multi-project'() {
        when:
        helper.create(['sub'])

        then:
        new File(projectDir, 'sub').exists()
        settingsFile.text.contains "include 'sub'"
    }

    def 'created multi-project can run build'() {
        helper.create(['sub'])

        buildFile << '''\
            subprojects {
                apply plugin: 'java'
            }
        '''.stripIndent()

        when:
        def result = runTasksSuccessfully('build')

        then:
        noExceptionThrown()
        result.standardOutput.contains ':sub:build'
        result.standardOutput.contains 'BUILD SUCCESSFUL'
    }

    def 'can create multi-projects with deeper directory structure'() {
        when:
        helper.create(['structure/sub'])

        then:
        def structure = new File(projectDir, 'structure')
        structure.isDirectory()
        new File(structure, 'sub').isDirectory()
        settingsFile.text.contains "include 'structure/sub'"
    }

    def 'add a subproject and build.gradle'() {
        String subBuildGradle = '''\
            apply plugin: 'java'
        '''.stripIndent()

        when:
        File directory = helper.addSubproject('sub', subBuildGradle)

        then:
        new File(directory, 'build.gradle').text == subBuildGradle
    }
}
