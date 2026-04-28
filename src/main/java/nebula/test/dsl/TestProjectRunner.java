package nebula.test.dsl;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstraction over the execution of TestKit's GradleRunner.
 * An instance of this class carries context with it from the project builder such as the project directory.
 */
@NullMarked
public class TestProjectRunner {
    private final File projectDir;

    TestProjectRunner(File projectDir) {
        this.projectDir = projectDir;
    }

    /**
     * runs given tasks using the standard runner, expecting a successful build
     *
     * @param args tasks to run
     * @return the BuildResult
     */
    public BuildResult run(String... args) {
        return run(GradleRunner.create().forwardOutput(), Arrays.asList(args));
    }

    /**
     * runs given tasks, expecting a successful build
     *
     * @param gradleRunner custom runner
     * @param args         tasks to run
     * @return the BuildResult
     */
    public BuildResult run(GradleRunner gradleRunner, String... args) {
        return run(gradleRunner, Arrays.asList(args));
    }

    /**
     * runs given tasks, expecting a successful build
     *
     * @param gradleRunner custom runner
     * @param args         tasks to run
     * @return the BuildResult
     */
    public BuildResult run(GradleRunner gradleRunner, List<String> args) {
        final List<String> fullArgsList = new ArrayList<>(args);
        fullArgsList.add("--warning-mode=all");
        return gradleRunner
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(fullArgsList)
                .build();
    }

    /**
     * runs given tasks using the standard runner, expecting a failed build
     *
     * @param args tasks to run
     * @return the BuildResult
     */
    public BuildResult runAndFail(String... args) {
        return runAndFail(GradleRunner.create().forwardOutput(), args);
    }

    /**
     * runs given tasks, expecting a failed build
     *
     * @param gradleRunner custom runner
     * @param args         tasks to run
     * @return the BuildResult
     */
    public BuildResult runAndFail(GradleRunner gradleRunner, String... args) {
        return runAndFail(gradleRunner, Arrays.asList(args));
    }

    /**
     * runs given tasks, expecting a failed build
     *
     * @param gradleRunner custom runner
     * @param args         tasks to run
     * @return the BuildResult
     */
    public BuildResult runAndFail(GradleRunner gradleRunner, List<String> args) {
        final List<String> fullArgsList = new ArrayList<>(args);
        fullArgsList.add("--warning-mode=all");
        return gradleRunner
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(fullArgsList)
                .buildAndFail();
    }
}
