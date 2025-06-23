package nebula.test.functional.internal.classpath

import nebula.test.ProjectSpec

class ClasspathAddingInitScriptBuilderIntegrationTest extends ProjectSpec {
    def 'can build init script with huge amount of dependencies'() {
        given:
        File initScript = project.file('build/init.gradle')
        List<File> libs = ClasspathAddingInitScriptBuilderFixture.createLibraries(projectDir)

        when:
        ClasspathAddingInitScriptBuilder.build(initScript, libs)

        then:
        initScript.exists()
        String initScriptContent = initScript.text

        libs.each { lib ->
            assert initScriptContent.contains("classpath files('${ClasspathAddingInitScriptBuilder.escapeString(lib.getAbsolutePath())}')")
        }
    }
}
