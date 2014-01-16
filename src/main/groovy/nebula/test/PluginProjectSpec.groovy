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
