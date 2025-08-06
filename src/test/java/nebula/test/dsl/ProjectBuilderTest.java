package nebula.test.dsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectBuilderTest {
    @Test
    public void testJavaPlugin(@TempDir File testProjectDir) {
        final var instance = new ProjectBuilder(testProjectDir);
        instance.plugins().java();
        instance.build();

        assertThat(testProjectDir.toPath().resolve("build.gradle.kts")).content()
                .contains("""
                        plugins {
                            id("java")
                        }""");
    }

    @Test
    public void testPlugin(@TempDir File testProjectDir) {
        final var instance = new ProjectBuilder(testProjectDir);
        instance.plugins().id("groovy");
        instance.build();

        assertThat(testProjectDir.toPath().resolve("build.gradle.kts")).content()
                .contains("""
                        plugins {
                            id("groovy")
                        }""");
    }

    @Test
    public void testDependencies(@TempDir File testProjectDir) {
        final var instance = new ProjectBuilder(testProjectDir);
        instance.dependencies("""
                testImplementation("org.assertj:assertj-core:3.27.3")""");
        instance.build();

        assertThat(testProjectDir.toPath().resolve("build.gradle.kts")).content()
                .contains("""
                        dependencies {
                            testImplementation("org.assertj:assertj-core:3.27.3")
                        }""");
    }

    @Test
    public void testSourceSet(@TempDir File testProjectDir) {
        final var instance = new ProjectBuilder(testProjectDir);
        instance.src().sourceSet("integTest").java("netflix/Test.java",
                // language=java
                """
                        package netflix;
                        class Test {
                        }
                        """);
        instance.build();

        assertThat(testProjectDir.toPath().resolve("src/integTest/java/netflix/Test.java")).exists();
    }
}
