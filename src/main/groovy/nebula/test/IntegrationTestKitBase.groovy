/*
 * Copyright 2016-2018 Netflix, Inc.
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

import nebula.test.functional.internal.classpath.ClasspathAddingInitScriptBuilder
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GFileUtils

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

/**
 * Base trait for implementing gradle integration tests using the {@code gradle-test-kit} runner.
 */
abstract trait IntegrationTestKitBase extends IntegrationBase {
    static final String LINE_END = System.getProperty('line.separator')
    boolean keepFiles = false
    boolean debug
    File buildFile
    File settingsFile
    String gradleVersion
    String gradleDistribution
    boolean forwardOutput = false

    /**
     * Automatic addition of `GradleRunner.withPluginClasspath()` _only_ works if the plugin under test is applied using the plugins DSL
     * This enables us to add the plugin-under-test classpath via an init script
     * https://docs.gradle.org/4.6/userguide/test_kit.html#sub:test-kit-automatic-classpath-injection
     */
    boolean definePluginOutsideOfPluginBlock = false

    @Override
    def initialize(String testMethodName) {
        super.initialize(testMethodName)
        if (! settingsFile) {
            settingsFile = new File(projectDir, "settings.gradle")
            settingsFile.text = "rootProject.name='${moduleName}'\n"
        }
        buildFile = new File(projectDir, "build.gradle")
    }

    def cleanup() {
        if (!keepFiles) {
            projectDir.deleteDir()
        }
    }

    File addSubproject(String name) {
        File subprojectDir = new File(projectDir, name)
        subprojectDir.mkdirs()
        settingsFile << "include \"${name}\"${LINE_END}"
        return subprojectDir
    }

    File addSubproject(String name, String buildGradle) {
        def subdir = addSubproject(name)
        new File(subdir, "build.gradle").text = buildGradle
        return subdir
    }

    BuildResult runTasks(String... tasks) {
        BuildResult result = createRunner(tasks)
                .build()
        return checkForDeprecations(result)
    }

    BuildResult runTasksAndFail(String... tasks) {
        BuildResult result = createRunner(tasks)
                .buildAndFail()
        return checkForDeprecations(result)
    }

    def tasksWereSuccessful(BuildResult result, String... tasks) {
        tasks.each { task ->
            if (!task.contains('-P') && !task.contains('--')) {
                def modTask = task.startsWith(':') ? task : ":$task"
                def outcome = result.task(modTask).outcome
                assert outcome == SUCCESS || outcome == UP_TO_DATE
            }
        }
    }

    GradleRunner createRunner(String... tasks) {
        def pluginArgs = definePluginOutsideOfPluginBlock ? createGradleTestKitInitArgs() : new ArrayList<>()
        def gradleRunnerBuilder = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(calculateArguments(tasks) + pluginArgs)
                .withDebug(debug)
                .withPluginClasspath()

        if(forwardOutput) {
            gradleRunnerBuilder.forwardOutput()
        }
        if (gradleVersion != null) {
            gradleRunnerBuilder.withGradleVersion(gradleVersion)
        }
        if (gradleDistribution != null) {
            gradleRunnerBuilder.withGradleDistribution(URI.create(gradleDistribution))
        }
        return gradleRunnerBuilder
    }

    private List<String> createGradleTestKitInitArgs() {
        File testKitDir = new File(projectDir, ".gradle-test-kit")
        if (!testKitDir.exists()) {
            GFileUtils.mkdirs(testKitDir)
        }

        File initScript = new File(testKitDir, "init.gradle")
        ClassLoader classLoader = this.getClass().getClassLoader()
        def classpathFilter = nebula.test.functional.GradleRunner.CLASSPATH_DEFAULT
        ClasspathAddingInitScriptBuilder.build(initScript, classLoader, classpathFilter)

        return Arrays.asList("--init-script", initScript.getAbsolutePath())
    }

    BuildResult checkForDeprecations(BuildResult result) {
        checkForDeprecations(result.output)
        return result
    }
}
