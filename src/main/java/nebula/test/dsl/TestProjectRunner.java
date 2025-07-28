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

    BuildResult run(String... args) {
        return run(GradleRunner.create().forwardOutput(), Arrays.asList(args));
    }

    BuildResult run(GradleRunner gradleRunner, String... args) {
        return run(gradleRunner, Arrays.asList(args));
    }

    BuildResult run(GradleRunner gradleRunner, List<String> args) {
        final var fullArgsList = new ArrayList<>(args);
        fullArgsList.add("--warning-mode=all");
        return gradleRunner
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(fullArgsList)
                .build();
    }

    BuildResult runAndFail(String... args) {
        return runAndFail(GradleRunner.create().forwardOutput(), args);
    }

    BuildResult runAndFail(GradleRunner gradleRunner, String... args) {
        return runAndFail(gradleRunner, Arrays.asList(args));
    }

    BuildResult runAndFail(GradleRunner gradleRunner, List<String> args) {
        final var fullArgsList = new ArrayList<>(args);
        fullArgsList.add("--warning-mode=all");
        return gradleRunner
                .withProjectDir(projectDir)
                .withPluginClasspath()
                .withArguments(fullArgsList)
                .buildAndFail();
    }
}
