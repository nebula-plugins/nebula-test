package nebula.test.dsl;

import nebula.test.SupportedGradleVersion;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static nebula.test.dsl.TestKitAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class TestKitAssertionsTest {

    @Test
    public void testTaskResult(@TempDir File testProjectDir) {
        final var builder = TestProjectBuilder.testProject(testProjectDir);
        builder.rootProject().plugins().java();
        final var runner = builder.build();

        final var result = runner.run(
                GradleRunner.create().withGradleVersion(SupportedGradleVersion.MIN.version),
                "build");

        assertThat(result).hasNoDeprecationWarnings();
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.NO_SOURCE);
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS);
    }

    @Test
    public void testTaskResultInvalid(@TempDir File testProjectDir) {
        final var builder = TestProjectBuilder.testProject(testProjectDir);
        builder.rootProject().plugins().java();
        final var runner = builder.build();

        final var result = runner.run(
                GradleRunner.create().withGradleVersion(SupportedGradleVersion.MIN.version),
                "build");
        assertThatCode(() -> assertThat(result).task(":compileJava2").hasOutcome(TaskOutcome.NO_SOURCE))
                .isInstanceOf(AssertionError.class);
    }

    @Test
    public void testDeprecations(@TempDir File testProjectDir) {
        final var builder = TestProjectBuilder.testProject(testProjectDir);
        final var project = builder.rootProject();
        project.plugins().java();
        project.rawBuildScript("""
                open class Deprecate : DefaultTask() {
                    @TaskAction
                    fun action() {
                        this.project.logger.lifecycle("deprecated project usage")
                    }
                }
                tasks.create<Deprecate>("deprecate") {
                }
                """);
        final var runner = builder.build();

        final var result = runner.run(
                GradleRunner.create().withGradleVersion(SupportedGradleVersion.MAX.version).forwardOutput(),
                "deprecate");
        assertThatCode(() -> assertThat(result).hasNoDeprecationWarnings())
                .isInstanceOf(AssertionError.class);
    }
}
