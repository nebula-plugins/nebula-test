/*
 * Copyright 2013-2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nebula.test

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.gradle.api.logging.LogLevel

import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean

/**
 * Base class which provides useful methods for testing a gradle plugin.
 *
 * <p>This is testing framework agnostic and can be either extended (see {@link BaseIntegrationSpec}) or composed, by
 * including it inside a test class as field.
 */
@CompileStatic
abstract trait IntegrationBase {
    File projectDir
    String moduleName
    LogLevel logLevel = LogLevel.LIFECYCLE
    List<File> initScripts = []
    boolean parallelEnabled = false

    private static final String LOGGING_LEVEL_ENV_VARIABLE = "NEBULA_TEST_LOGGING_LEVEL"

    def initialize(Class<?> testClass, String testMethodName, String baseFolderName = 'nebulatest') {
        projectDir = new File("build/${baseFolderName}/${testClass.canonicalName}/${testMethodName.replaceAll(/\W+/, '-')}").absoluteFile
        if (projectDir.exists()) {
            projectDir.deleteDir()
        }
        projectDir.mkdirs()
        moduleName = this.findModuleName()
    }

    /**
     * Override to alter its value
     * @return
     */
    LogLevel getLogLevel() {
        String levelFromEnv = System.getenv(LOGGING_LEVEL_ENV_VARIABLE)
        if(!levelFromEnv) {
            return logLevel
        }
        return LogLevel.valueOf(levelFromEnv.toUpperCase())
    }

    void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel
    }

    /* Setup */

    File directory(String path, File baseDir = getProjectDir()) {
        new File(baseDir, path).with {
            mkdirs()
            it
        }
    }

    File file(String path, File baseDir = getProjectDir()) {
        def splitted = path.split('/')
        def directory = splitted.size() > 1 ? directory(splitted[0..-2].join('/'), baseDir) : baseDir
        def file = new File(directory, splitted[-1])
        file.createNewFile()
        file
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    File createFile(String path, File baseDir = getProjectDir()) {
        File file = file(path, baseDir)
        if (!file.exists()) {
            assert file.parentFile.mkdirs() || file.parentFile.exists()
            file.createNewFile()
        }
        file
    }

    static void checkOutput(String output) {
        outputBuildScan(output)
        checkForMutableProjectState(output)
        checkForDeprecations(output)
    }

    static void outputBuildScan(String output) {
        boolean foundPublishingLine = false
        output.readLines().find {line ->
            if (foundPublishingLine) {
                if (line.startsWith("http")) {
                    println("Build scan: $line")
                } else {
                    println("Build scan was enabled but did not publish: $line")
                }
                return true
            }
            if (line == "Publishing build scan...") {
                foundPublishingLine = true
            }
            return false
        }
    }

    static void checkForDeprecations(String output) {
        def deprecations = output.readLines().findAll {
            it.contains("has been deprecated and is scheduled to be removed in Gradle") ||
                    it.contains("Deprecated Gradle features were used in this build") ||
                    it.contains("has been deprecated. This is scheduled to be removed in Gradle") ||
                    it.contains("This will fail with an error in Gradle") ||
                    it.contains("This behaviour has been deprecated and is scheduled to be removed in Gradle")
        }
        // temporary for known issue with overwriting task
        // overridden task expected to not be needed in future version
        if (deprecations.size() == 1 && deprecations.first().contains("Creating a custom task named 'dependencyInsight' has been deprecated and is scheduled to be removed in Gradle 5.0.")) {
            return
        }
        if (!System.getProperty("ignoreDeprecations") && !deprecations.isEmpty()) {
            throw new IllegalArgumentException("Deprecation warnings were found (Set the ignoreDeprecations system property during the test to ignore):\n" + deprecations.collect {
                " - $it"
            }.join("\n"))
        }
    }

    static void checkForMutableProjectState(String output) {
        def mutableProjectStateWarnings = output.readLines().findAll {
            it.contains("was resolved without accessing the project in a safe manner") ||
                    it.contains("This may happen when a configuration is resolved from a thread not managed by Gradle or from a different project") ||
                    it.contains("was resolved from a thread not managed by Gradle.") ||
                    it.contains("was attempted from a context different than the project context")

        }

        if (!System.getProperty("ignoreMutableProjectStateWarnings") && !mutableProjectStateWarnings.isEmpty()) {
            throw new IllegalArgumentException("Mutable Project State warnings were found (Set the ignoreMutableProjectStateWarnings system property during the test to ignore):\n" + mutableProjectStateWarnings.collect {
                " - $it"
            }.join("\n"))
        }
    }

    void writeHelloWorld(File baseDir = getProjectDir()) {
        writeHelloWorld('nebula', baseDir)
    }

    void writeHelloWorld(String packageDotted, File baseDir = getProjectDir()) {
        writeJavaSourceFile("""\
            package ${packageDotted};
        
            public class HelloWorld {
                public static void main(String[] args) {
                    System.out.println("Hello Integration Test");
                }
            }
            """.stripIndent(), 'src/main/java', baseDir)
    }

    void writeJavaSourceFile(String source, File projectDir  = getProjectDir()) {
        writeJavaSourceFile(source, 'src/main/java', projectDir)
    }

    void writeJavaSourceFile(String source, String sourceFolderPath, File projectDir = getProjectDir()) {
        File javaFile = createFile(sourceFolderPath + '/' + fullyQualifiedName(source).replaceAll(/\./, '/') + '.java', projectDir)
        javaFile.text = source
    }

    /**
     * Creates a passing unit test for testing your plugin.
     * @param baseDir the directory to begin creation from, defaults to projectDir
     */
    void writeUnitTest(File baseDir = getProjectDir()) {
        writeTest('src/test/java/', 'nebula', false, baseDir)
    }

    /**
     * Creates a unit test for testing your plugin.
     * @param failTest true if you want the test to fail, false if the test should pass
     * @param baseDir the directory to begin creation from, defaults to projectDir
     */
    void writeUnitTest(boolean failTest, File baseDir = getProjectDir()) {
        writeTest('src/test/java/', 'nebula', failTest, baseDir)
    }

    void writeUnitTest(String source, File baseDir = getProjectDir()) {
        writeJavaSourceFile(source, 'src/test/java', baseDir)
    }

    /**
     *
     * Creates a unit test for testing your plugin.
     * @param srcDir the directory in the project where the source file should be created.
     * @param packageDotted the package for the unit test class, written in dot notation (ex. - nebula.integration)
     * @param failTest true if you want the test to fail, false if the test should pass
     * @param baseDir the directory to begin creation from, defaults to projectDir
     */
    void writeTest(String srcDir, String packageDotted, boolean failTest, File baseDir = getProjectDir()) {
        writeJavaSourceFile("""\
            package ${packageDotted};
            import org.junit.Test;
            import static org.junit.Assert.assertFalse;
    
            public class HelloWorldTest {
                @Test public void doesSomething() {
                    assertFalse( $failTest ); 
                }
            }
            """.stripIndent(), srcDir, baseDir)
    }

    private String fullyQualifiedName(String sourceStr) {
        def pkgMatcher = sourceStr =~ /\s*package\s+([\w\.]+)/
        def pkg = pkgMatcher.find() ? (pkgMatcher[0] as List<String>)[1] + '.' : ''

        def classMatcher = sourceStr =~ /\s*(class|interface)\s+(\w+)/
        return classMatcher.find() ? pkg + (classMatcher[0] as List<String>)[2] : null
    }

    /**
     * Creates a properties file to included as project resource.
     * @param srcDir the directory in the project where the source file should be created.
     * @param fileName to be used for the file, sans extension.  The .properties extension will be added to the name.
     * @param baseDir the directory to begin creation from, defaults to projectDir
     */
    void writeResource(String srcDir, String fileName, File baseDir = getProjectDir()) {
        def path = "$srcDir/${fileName}.properties"
        def resourceFile = createFile(path, baseDir)
        resourceFile.text = "firstProperty=foo.bar"
    }

    void addResource(String srcDir, String filename, String contents, File baseDir = getProjectDir()) {
        def resourceFile = createFile("${srcDir}/${filename}", baseDir)
        resourceFile.text = contents
    }

    String findModuleName() {
        getProjectDir().getName().replaceAll(/_\d+/, '')
    }

    List<String> calculateArguments(String... args) {
        List<String> arguments = []
        // Gradle will use these files name from the PWD, instead of the project directory. It's easier to just leave
        // them out and let the default find them, since we're not changing their default names.
        //arguments += '--build-file'
        //arguments += (buildFile.canonicalPath - projectDir.canonicalPath).substring(1)
        //arguments += '--settings-file'
        //arguments += (settingsFile.canonicalPath - projectDir.canonicalPath).substring(1)
        //arguments += '--no-daemon'
        switch (getLogLevel()) {
            case LogLevel.INFO:
                arguments += '--info'
                break
            case LogLevel.DEBUG:
                arguments += '--debug'
                break
        }
        if(parallelEnabled) {
            arguments += '--parallel'
        }
        arguments += '--stacktrace'
        arguments += '-Dorg.gradle.warning.mode=all'
        arguments.addAll(args)
        arguments.addAll(initScripts.collect { file -> '-I' + file.absolutePath })
        arguments
    }

    static def dependencies(File _buildFile, String... confs = ['compile', 'testCompile', 'implementation', 'testImplementation', 'api']) {
        _buildFile.text.readLines()
                .collect { it.trim() }
                .findAll { line -> confs.any { c -> line.startsWith(c) } }
                .collect { it.split(/\s+/)[1].replaceAll(/['"]/, '') }
                .sort()
    }

}
