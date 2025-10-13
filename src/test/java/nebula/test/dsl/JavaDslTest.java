package nebula.test.dsl;

import nebula.test.SupportedGradleVersion;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.File;

import static nebula.test.dsl.TestKitAssertions.assertThat;

public class JavaDslTest {

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion.class)
    public void testJavaDsl(SupportedGradleVersion gradleVersion, @TempDir File testProjectDir) {
        final var builder = TestProjectBuilder.testProject(testProjectDir);
        builder.properties().property("test","value");
        builder.rootProject().plugins().java();
        final var runner = builder.build();

        final var result = runner.run(
                GradleRunner.create().withGradleVersion(gradleVersion.version),
                "build");

        assertThat(result)
                .hasNoDeprecationWarnings()
                .hasNoMutableStateWarnings();
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.NO_SOURCE);
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS);
        assertThat(testProjectDir.toPath().resolve("gradle.properties"))
                .exists()
                .content()
                .contains("test=value");
    }
}
