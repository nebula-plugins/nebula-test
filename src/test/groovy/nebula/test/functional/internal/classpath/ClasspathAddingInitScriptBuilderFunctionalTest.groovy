package nebula.test.functional.internal.classpath

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

class ClasspathAddingInitScriptBuilderFunctionalTest extends IntegrationSpec {
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
        ExecutionResult executionResult = runTasksSuccessfully('helloWorld', '--init-script', initScript.canonicalPath)

        then:
        executionResult.standardOutput.contains('Hello World!')
    }
}
