package nebula.test.functional.internal.classpath

import nebula.test.ProjectSpec
import org.gradle.util.TextUtil

class ClasspathAddingInitScriptBuilderIntegrationTest extends ProjectSpec {
    ClasspathAddingInitScriptBuilder builder = new ClasspathAddingInitScriptBuilder()

    def "can build init script with huge amount of dependencies"() {
        given:
        File initScript = project.file('build/init.gradle')
        List<File> libs = ClasspathAddingInitScriptBuilderFixture.createLibraries(projectDir)

        when:
        builder.build(initScript, libs)

        then:
        initScript.exists()
        String initScriptContent = initScript.text

        libs.each { lib ->
            assert initScriptContent.contains("classpath files('${TextUtil.escapeString(lib.getAbsolutePath())}')")
        }
    }
}
