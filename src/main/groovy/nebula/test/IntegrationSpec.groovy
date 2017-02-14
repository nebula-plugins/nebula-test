/*
 * Copyright 2013 the original author or authors.
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

import com.energizedwork.spock.extensions.TempDirectory
import com.google.common.base.Predicate
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import nebula.test.functional.ExecutionResult
import nebula.test.functional.GradleRunner
import nebula.test.functional.GradleRunnerFactory
import nebula.test.functional.PreExecutionAction
import nebula.test.functional.internal.GradleHandle
import nebula.test.multiproject.MultiProjectIntegrationHelper
import org.apache.commons.io.FileUtils
import org.gradle.api.logging.LogLevel
import spock.lang.Specification

/**
 * @author Justin Ryan
 * @author Marcin Erdmann
 */
@CompileStatic
abstract class IntegrationSpec extends Specification {
    private static final String DEFAULT_REMOTE_DEBUG_JVM_ARGUMENTS = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
    private static final Integer DEFAULT_DAEMON_MAX_IDLE_TIME_IN_SECONDS_IN_MEMORY_SAFE_MODE = 15;

    @TempDirectory(clean=false) protected File projectDir

    // Holds State of last run
    private ExecutionResult result

    protected String gradleVersion
    protected LogLevel logLevel = LogLevel.INFO

    protected String moduleName
    protected File settingsFile
    protected File buildFile
    protected boolean fork = false
    protected boolean remoteDebug = false
    protected List<String> jvmArguments = []
    protected MultiProjectIntegrationHelper helper
    protected Predicate<URL> classpathFilter
    protected List<File> initScripts = []
    protected List<PreExecutionAction> preExecutionActions = []
    //Shutdown Gradle daemon after a few seconds to release memory. Useful for testing with multiple Gradle versions on shared CI server
    protected boolean memorySafeMode = false
    protected Integer daemonMaxIdleTimeInSecondsInMemorySafeMode = DEFAULT_DAEMON_MAX_IDLE_TIME_IN_SECONDS_IN_MEMORY_SAFE_MODE

    private String findModuleName() {
        getProjectDir().getName().replaceAll(/_\d+/, '')
    }

    def setup() {
        System.setProperty('gradle.user.home', buildDir.resolve('.gradle').toString())

        moduleName = findModuleName()
        if (!settingsFile) {
            settingsFile = new File(getProjectDir(), 'settings.gradle')
            settingsFile.text = "rootProject.name='${moduleName}'\n"
        }

        if (!buildFile) {
            buildFile = new File(getProjectDir(), 'build.gradle')
        }

        println "Running test from ${getProjectDir()}"

        buildFile << "// Running test for ${moduleName}\n"

        helper = new MultiProjectIntegrationHelper(getProjectDir(), settingsFile)
    }

    protected GradleHandle launcher(String... args) {
        List<String> arguments = calculateArguments(args)
        List<String> jvmArguments = calculateJvmArguments()
        Integer daemonMaxIdleTimeInSeconds = calculateMaxIdleDaemonTimeoutInSeconds()

        GradleRunner runner = GradleRunnerFactory.createTooling(fork, gradleVersion, daemonMaxIdleTimeInSeconds, classpathFilter)
        runner.handle(getProjectDir(), arguments, jvmArguments, preExecutionActions)
    }

    private List<String> calculateArguments(String... args) {
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
        arguments += '--stacktrace'
        arguments.addAll(args)
        arguments.addAll(initScripts.collect { file -> '-I' + file.absolutePath })
        arguments
    }

    private List<String> calculateJvmArguments() {
        return jvmArguments + (remoteDebug ? [DEFAULT_REMOTE_DEBUG_JVM_ARGUMENTS] : [] as List) as List
    }

    private Integer calculateMaxIdleDaemonTimeoutInSeconds() {
        return memorySafeMode ? daemonMaxIdleTimeInSecondsInMemorySafeMode : null
    }

    protected void addInitScript(File initFile) {
        initScripts.add(initFile)
    }

    protected void addPreExecute(PreExecutionAction preExecutionAction) {
        preExecutionActions.add(preExecutionAction)
    }

    /**
     * Override to alter its value
     * @return
     */
    protected LogLevel getLogLevel() {
        return logLevel
    }

    /* Setup */
    protected File directory(String path, File baseDir = getProjectDir()) {
        new File(baseDir, path).with {
            mkdirs()
            it
        }
    }

    protected File file(String path, File baseDir = getProjectDir()) {
        def splitted = path.split('/')
        def directory = splitted.size() > 1 ? directory(splitted[0..-2].join('/'), baseDir) : baseDir
        def file = new File(directory, splitted[-1])
        file.createNewFile()
        file
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    protected File createFile(String path, File baseDir = getProjectDir()) {
        File file = file(path, baseDir)
        if (!file.exists()) {
            assert file.parentFile.mkdirs() || file.parentFile.exists()
            file.createNewFile()
        }
        file
    }

    protected void writeHelloWorld(String packageDotted, File baseDir = getProjectDir()) {
        def path = 'src/main/java/' + packageDotted.replace('.', '/') + '/HelloWorld.java'
        def javaFile = createFile(path, baseDir)
        javaFile << """package ${packageDotted};

            public class HelloWorld {
                public static void main(String[] args) {
                    System.out.println("Hello Integration Test");
                }
            }
        """.stripIndent()
    }

    /**
     * Creates a unit test for testing your plugin.
     * @param failTest true if you want the test to fail, false if the test should pass
     * @param baseDir the directory to begin creation from, defaults to projectDir
     */
    protected void writeUnitTest(boolean failTest, File baseDir = getProjectDir()) {
        writeTest('src/test/java/', 'nebula', failTest, baseDir)
    }

    /**
     *
     * Creates a unit test for testing your plugin.
     * @param srcDir the directory in the project where the source file should be created.
     * @param packageDotted the package for the unit test class, written in dot notation (ex. - nebula.integration)
     * @param failTest true if you want the test to fail, false if the test should pass
     * @param baseDir the directory to begin creation from, defaults to projectDir
     */
    protected void writeTest(String srcDir, String packageDotted, boolean failTest, File baseDir = getProjectDir()) {
        def path = srcDir + packageDotted.replace('.', '/') + '/HelloWorldTest.java'
        def javaFile = createFile(path, baseDir)
        javaFile << """package ${packageDotted};
            import org.junit.Test;
            import static org.junit.Assert.assertFalse;

            public class HelloWorldTest {
                @Test public void doesSomething() {
                    assertFalse( $failTest );
                }
            }
        """.stripIndent()
    }

    /**
     * Creates a properties file to included as project resource.
     * @param srcDir the directory in the project where the source file should be created.
     * @param fileName to be used for the file, sans extension.  The .properties extension will be added to the name.
     * @param baseDir the directory to begin creation from, defaults to projectDir
     */
    protected void writeResource(String srcDir, String fileName, File baseDir = getProjectDir()) {
        def path = "$srcDir/${fileName}.properties"
        def resourceFile = createFile(path, baseDir)
        resourceFile.text = "firstProperty=foo.bar"
    }

    protected void copyResources(String srcDir, String destination) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(srcDir);
        if (resource == null) {
            throw new RuntimeException("Could not find classpath resource: $srcDir")
        }

        File destinationFile = file(destination)
        File resourceFile = new File(resource.toURI())
        if (resourceFile.file) {
            FileUtils.copyFile(resourceFile, destinationFile)
        } else {
            FileUtils.copyDirectory(resourceFile, destinationFile)
        }
    }

    protected String applyPlugin(Class pluginClass) {
        "apply plugin: $pluginClass.name"
    }

    /* Checks */
    protected boolean fileExists(String path) {
        new File(projectDir, path).exists()
    }

    @Deprecated
    protected boolean wasExecuted(String taskPath) {
        result.wasExecuted(taskPath)
    }

    @Deprecated
    protected boolean wasUpToDate(String taskPath) {
        result.wasUpToDate(taskPath)
    }

    @Deprecated
    protected String getStandardError() {
        result.standardError
    }

    @Deprecated
    protected String getStandardOutput() {
        result.standardOutput
    }

    /* Execution */
    protected ExecutionResult runTasksSuccessfully(String... tasks) {
        ExecutionResult result = runTasks(tasks)
        if (result.failure) {
            result.rethrowFailure()
        }
        result
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    protected ExecutionResult runTasksWithFailure(String... tasks) {
        ExecutionResult result = runTasks(tasks)
        assert result.failure
        result
    }

    protected ExecutionResult runTasks(String... tasks) {
        ExecutionResult result = launcher(tasks).run()
        this.result = result
        return result
    }

    protected File addSubproject(String subprojectName) {
        helper.addSubproject(subprojectName)
    }

    protected File addSubproject(String subprojectName, String subBuildGradleText) {
        helper.addSubproject(subprojectName, subBuildGradleText)
    }

    File getProjectDir() {
        return projectDir
    }

    File getSettingsFile() {
        return settingsFile
    }
}
