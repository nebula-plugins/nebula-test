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

import org.apache.commons.io.FileUtils
import org.gradle.BuildAdapter
import org.gradle.BuildResult
import org.gradle.GradleLauncher
import org.gradle.StartParameter
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.TaskState
import org.gradle.initialization.ClassLoaderRegistry
import org.gradle.initialization.DefaultGradleLauncher
import org.gradle.internal.classloader.FilteringClassLoader
import org.gradle.invocation.BuildClassLoaderRegistry
import org.gradle.invocation.DefaultGradle
import org.gradle.logging.ShowStacktrace
import org.gradle.logging.internal.StreamBackedStandardOutputListener
import spock.lang.Specification
import spock.util.mop.Use
import com.energizedwork.spock.extensions.TempDirectory

/**
 * @author Marcin Erdmann
 */
abstract class IntegrationSpec extends Specification {
    @TempDirectory(clean=false) File projectDir

    // Holds State of last run
    // TODO Put these in their own data structure along with BuildResult
    private final StringBuilder captureError = new StringBuilder()
    private final StringBuilder captureOutput = new StringBuilder()
    protected List<ExecutedTask> executedTasks = []

    String moduleName
    File settingsFile
    File buildFile

    String findModuleName() {
        projectDir.getName().replaceAll(/_\d+/, '')
    }

    def setup() {
        moduleName = findModuleName()
        if (!settingsFile) {
            settingsFile = new File(projectDir, 'settings.gradle')
            settingsFile.text = "rootProject.name='${moduleName}'"
        }

        if (!buildFile) {
            buildFile = new File(projectDir, 'build.gradle')
        }
        println "Running test from ${projectDir}"

        buildFile << "// Running test for ${moduleName}\n"
    }

    protected GradleLauncher launcher(String... args) {
        StartParameter startParameter = GradleLauncher.createStartParameter(args)
        startParameter.projectDir = projectDir
        startParameter.buildFile = buildFile
        startParameter.settingsFile = settingsFile
        startParameter.logLevel = getLogLevel()
        startParameter.showStacktrace = ShowStacktrace.ALWAYS

        DefaultGradleLauncher launcher = GradleLauncher.newInstance(startParameter)

        // Stdin/StdOut
        captureError.setLength(0)
        launcher.addStandardErrorListener(new StreamBackedStandardOutputListener(captureError))
        captureOutput.setLength(0)
        launcher.addStandardOutputListener(new StreamBackedStandardOutputListener(captureOutput))

        // Inject our classpath
        ((DefaultGradle) launcher.gradle).getServices().get(BuildClassLoaderRegistry).addRootClassLoader(getClass().classLoader)

        // Allowing packages and resources from our classpath, might be moot given above line
        launcher.addListener(new AllowListener(getAllowedPackages(), getAllowedResources()))

        // Executed Tasks
        executedTasks.clear()
        launcher.addListener(new TaskExecutionListener() {
            void beforeExecute(Task task) {
                executedTasks << new ExecutedTask(task: task)
            }

            void afterExecute(Task task, TaskState taskState) {
                executedTasks.last().state = taskState
            }
        })
        launcher
    }

    /* Override to customize */
    String[] getAllowedPackages() { [] }

    String[] getAllowedResources() { [] }

    /**
     * Override to alter its value
     * @return
     */
    LogLevel getLogLevel() {
        return LogLevel.INFO
    }

    /* Setup */
    File directory(String path) {
        new File(projectDir, path).with {
            mkdirs()
            it
        }
    }

    protected File file(String path) {
        def splitted = path.split('/')
        def directory = splitted.size() > 1 ? directory(splitted[0..-2].join('/')) : projectDir
        def file = new File(directory, splitted[-1])
        file.createNewFile()
        file
    }

    File createFile(String path) {
        File file = file(path)
        if (!file.exists()) {
            assert file.parentFile.mkdirs() || file.parentFile.exists()
            file.createNewFile()
        }
        file
    }

    def writeHelloWorld(String packageDotted) {
        def path = 'src/main/java/' + packageDotted.replaceAll('.', '/') + '/HelloWorld.java'
        def javaFile = createFile(path)
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
     */
    def writeUnitTest(boolean failTest) {
        writeTest('src/test/java', 'nebula', failTest)
    }

    /**
     *
     * Creates a unit test for testing your plugin.
     * @param srcDir the directory in the project where the source file should be created.
     * @param packageDotted the package for the unit test class, written in dot notation (ex. - nebula.integration)
     * @param failTest true if you want the test to fail, false if the test should pass
     */
    def writeTest(String srcDir, String packageDotted, boolean failTest) {
        def path = srcDir + packageDotted.replace('.', '/') + '/HelloWorldTest.java'
        def javaFile = createFile(path)
        javaFile << """package ${packageDotted};
            import org.junit.Test;

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
     */
    def writeResource(String srcDir, String fileName) {
        def path = "$srcDir/${fileName}.properties"
        def resourceFile = createFile(path)
        resourceFile.text = "firstProperty=foo.bar"
    }

    String copyResources(String srcDir, String destination) {
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

    String applyPlugin(Class pluginClass) {
        "apply plugin: project.class.classLoader.loadClass('$pluginClass.name')"
    }

    /* Checks */
    boolean fileExists(String path) {
        new File(projectDir, path).exists()
    }

    ExecutedTask task(String name) {
        executedTasks.find { it.task.name == name }
    }

    Collection<ExecutedTask> tasks(String... names) {
        def tasks = executedTasks.findAll { it.task.name in names }
        assert tasks.size() == names.size()
        tasks
    }

    boolean wasExecuted(String taskPath) {
        executedTasks.any { it.task.path == taskPath }
    }

    boolean wasUpToDate(String taskPath) {
        def taskstate = executedTasks.find { it.task.path == taskPath }?.state
        return taskstate?.skipped && taskstate?.skipMessage == 'UP-TO-DATE'
    }

    String getStandardError() {
        captureError.toString()
    }

    String getStandardOutput() {
        captureOutput.toString()
    }

    /* Execution */
    protected BuildResult runTasksSuccessfully(String... tasks) {
        BuildResult result = runTasks(tasks)
        if (result.failure) {
            result.rethrowFailure()
        }
        result
    }

	protected BuildResult runTasksWithFailure(String... tasks) {
		BuildResult result = runTasks(tasks)
		assert result.failure
		result
	}

	protected BuildResult runTasks(String... tasks) {
		launcher(tasks).run()
	}

    protected BuildResult analyze(String... tasks) {
        BuildResult result = launcher(tasks).buildAndRunAnalysis
        if(result.failure) {
            result.rethrowFailure()
        }
        result
    }
    /**
     * Via: https://github.com/jvoegele/gradle-android-plugin/blob/master/src/integTest/groovy/com/jvoegele/gradle/android/support/MyBuildListener.groovy
     */
    class AllowListener extends BuildAdapter {
        String[] allowedPackages
        String[] allowedResources

        AllowListener(String[] allowedPackages, String[] allowedResources = []) {
            this.allowedPackages = allowedPackages
            this.allowedResources = allowedResources
        }

        @Override
        void projectsLoaded(Gradle gradle) {
            // This is a hack: Gradle Android Plugin integration tests have Gradle Android Plugin itself on their classpath,
            // but Gradle lets the buildscript only access a few selected packages, not the full classpath. That is a good
            // idea per se, as we should make the test buildscripts self-contained anyway, but I wasn't able to make it work
            // (a 'buildscript { ... }' section in test buildscript isn't enough, something is missing...). So I simply
            // adjust the Gradle filtering classloader to allow access to Gradle Android Plugin. It is not a public API,
            // but it's highly unlikely that it will change before we switch to using Gradle Tooling API. See TestProject too.
            FilteringClassLoader rootClassloader = (FilteringClassLoader) ((DefaultGradle) gradle).rootProject.services.get(ClassLoaderRegistry.class).getGradleApiClassLoader()
            allowedPackages.each {
                rootClassloader.allowPackage(it)
            }
            allowedResources.each {
                rootClassloader.allowResources(it)
            }
        }

    }
}
