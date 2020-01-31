/*
 * Copyright 2013-2018 the original author or authors.
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
import org.gradle.api.logging.configuration.WarningMode

/**
 * @author Justin Ryan
 * @author Marcin Erdmann
 */
@CompileStatic
abstract class IntegrationSpec extends BaseIntegrationSpec {
    private static final String DEFAULT_REMOTE_DEBUG_JVM_ARGUMENTS = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
    private static final Integer DEFAULT_DAEMON_MAX_IDLE_TIME_IN_SECONDS_IN_MEMORY_SAFE_MODE = 15

    // Holds State of last run
    private ExecutionResult result

    protected String gradleVersion
    protected File settingsFile
    protected File buildFile
    protected boolean fork = false
    protected boolean remoteDebug = false
    protected List<String> jvmArguments = []
    protected MultiProjectIntegrationHelper helper
    protected Predicate<URL> classpathFilter
    protected List<PreExecutionAction> preExecutionActions = []
    //Shutdown Gradle daemon after a few seconds to release memory. Useful for testing with multiple Gradle versions on shared CI server
    protected boolean memorySafeMode = false
    protected Integer daemonMaxIdleTimeInSecondsInMemorySafeMode = DEFAULT_DAEMON_MAX_IDLE_TIME_IN_SECONDS_IN_MEMORY_SAFE_MODE



    def setup() {
        logLevel = LogLevel.INFO
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

    protected GradleHandle launcher(WarningMode warningMode, String ... args) {
        List<String> arguments = calculateArguments(warningMode, args)
        List<String> jvmArguments = calculateJvmArguments()
        Integer daemonMaxIdleTimeInSeconds = calculateMaxIdleDaemonTimeoutInSeconds()

        GradleRunner runner = GradleRunnerFactory.createTooling(fork, gradleVersion, daemonMaxIdleTimeInSeconds, classpathFilter)
        runner.handle(getProjectDir(), arguments, jvmArguments, preExecutionActions)
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
        return runTasksSuccessfully(WarningMode.Fail, tasks)
    }

    protected ExecutionResult runTasksSuccessfully(WarningMode warningMode, String... tasks) {
        ExecutionResult result = runTasks(warningMode, tasks)
        if (result.failure) {
            result.rethrowFailure()
        }
        result
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    protected ExecutionResult runTasksWithFailure(String... tasks) {
        return runTasksWithFailure(WarningMode.Fail, tasks)
    }

    @CompileStatic(TypeCheckingMode.SKIP)
    protected ExecutionResult runTasksWithFailure(WarningMode warningMode, String... tasks) {
        ExecutionResult result = runTasks(warningMode, tasks)
        assert result.failure
        result
    }

    protected ExecutionResult runTasks(String... tasks) {
        return runTasks(WarningMode.Fail, tasks)
    }

    protected ExecutionResult runTasks(WarningMode warningMode, String... tasks) {
        ExecutionResult result = launcher(warningMode, tasks).run()
        this.result = result
        return result
    }

    protected File addSubproject(String subprojectName) {
        helper.addSubproject(subprojectName)
    }

    protected File addSubproject(String subprojectName, String subBuildGradleText) {
        helper.addSubproject(subprojectName, subBuildGradleText)
    }

    File getSettingsFile() {
        return settingsFile
    }
}
