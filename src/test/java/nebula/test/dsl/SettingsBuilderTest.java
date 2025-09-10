package nebula.test.dsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class SettingsBuilderTest {
    @TempDir
    File testDir;

    @Test
    public void testGroovy() {
        final var builder = new SettingsBuilder(testDir);
        builder.includeProject("sub");
        builder.build(BuildscriptLanguage.GROOVY);
        assertThat(testDir.toPath().resolve("settings.gradle").toFile()).exists()
                .content().isEqualTo("""
                        include ':sub'
                        """);
    }

    @Test
    public void testKotlin() {
        final var builder = new SettingsBuilder(testDir);
        builder.includeProject("sub");
        builder.build(BuildscriptLanguage.KOTLIN);
        assertThat(testDir.toPath().resolve("settings.gradle.kts").toFile()).exists()
                .content().isEqualTo("""
                        include(":sub")
                        """);
    }
}
