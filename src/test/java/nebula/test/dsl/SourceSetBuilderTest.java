package nebula.test.dsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class SourceSetBuilderTest {
    @TempDir
    private File tempDir;

    @Test
    public void test_groovy() {
        SourceSetBuilder instance = new SourceSetBuilder(tempDir);
        instance.groovy("test.groovy", "testGroovy");
        assertThat(tempDir.toPath().resolve("groovy").resolve("test.groovy"))
                .exists()
                .hasContent("testGroovy");
    }

    @Test
    public void test_scala() {
        SourceSetBuilder instance = new SourceSetBuilder(tempDir);
        instance.scala("Test.scala", "class ScalaClass");
        assertThat(tempDir.toPath().resolve("scala").resolve("Test.scala"))
                .exists()
                .hasContent("class ScalaClass");
    }
}
