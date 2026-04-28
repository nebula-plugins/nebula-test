package nebula.test.dsl;

import nebula.test.SupportedGradleVersion;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.File;
import java.net.URI;

import static nebula.test.dsl.TestKitAssertions.assertThat;

class JavaDslTest {

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion.class)
    public void testJavaDsl(SupportedGradleVersion gradleVersion, @TempDir File testProjectDir) {
        final var builder = TestProjectBuilder.testProject(testProjectDir);
        builder.properties().property("test", "value");
        builder.rootProject().plugins().java();
        final var runner = builder.build();
        GradleRunner gradleRunner = GradleRunner.create();
        if (gradleVersion.version instanceof Gradle.GradleVersion) {
            gradleRunner.withGradleVersion(((Gradle.GradleVersion) gradleVersion.version).version());
        } else if (gradleVersion.version instanceof Gradle.GradleDistribution) {
            gradleRunner.withGradleDistribution(URI.create(((Gradle.GradleDistribution) gradleVersion.version).url()));
        }
        final var result = runner.run(gradleRunner, "build");

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
