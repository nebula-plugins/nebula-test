package nebula.test.dsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectPropertiesTest {
    @Test
    public void testProjectProperty(@TempDir File testProjectDir) {
        final var instance = new ProjectProperties(testProjectDir);
        instance.property("org.gradle.caching", "true");
        instance.build();

        assertThat(testProjectDir.toPath().resolve("gradle.properties"))
                .exists()
                .content()
                .contains("org.gradle.caching=true");
    }

    @Test
    public void testGradleCache(@TempDir File testProjectDir) {
        final var instance = new ProjectProperties(testProjectDir);
        instance.gradleCache(true);
        instance.build();

        assertThat(testProjectDir.toPath().resolve("gradle.properties"))
                .exists()
                .content()
                .contains("org.gradle.caching=true");
    }
}
