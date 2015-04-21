package nebula.test.functional.internal.classpath

import org.gradle.util.GFileUtils

final class ClasspathAddingInitScriptBuilderFixture {
    private ClasspathAddingInitScriptBuilderFixture() {}

    static List<File> createLibraries(File projectDir, int numberOfLibs = 500) {
        def libraries = []

        (1..numberOfLibs).each { counter ->
            File jar = new File(projectDir, "build/libs/lib${counter}.jar")
            GFileUtils.touch(jar)
            libraries << jar
        }

        libraries
    }
}
