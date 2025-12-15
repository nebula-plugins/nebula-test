package nebula.test.functional.internal.classpath

import nebula.test.IntegrationTestKitSpec
import org.gradle.testkit.runner.BuildResult

class ClasspathAddingInitScriptBuilderFunctionalTest extends IntegrationTestKitSpec {
    def "can use generated init script with huge amount of dependencies"() {
        given:
        File initScript = new File(projectDir, 'build/init.gradle')
        List<File> libs = ClasspathAddingInitScriptBuilderFixture.createLibraries(projectDir)
        ClasspathAddingInitScriptBuilder.build(initScript, libs)

        buildFile << """
task helloWorld {
    doLast {
        logger.quiet 'Hello World!'
    }
}
"""
        when:
        BuildResult result = runTasks('helloWorld', '--init-script', initScript.canonicalPath)

        then:
        result.output.contains('Hello World!')
    }
}
