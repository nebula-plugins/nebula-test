package nebula.test.dsl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PluginsBuilderTest {

    @Test
    public void testGroovyId(){
        final var builder = new PluginsBuilder();
        builder.id("java");
        final var actual = builder.build(BuildscriptLanguage.GROOVY,0);
        //language=groovy
        assertThat(actual).isEqualTo("""
                plugins {
                    id 'java'
                }
                """);
    }

    @Test
    public void testGroovyIdIndent(){
        final var builder = new PluginsBuilder();
        builder.id("java");
        final var actual = builder.build(BuildscriptLanguage.GROOVY,4);
        //language=groovy
        assertThat(actual).isEqualTo("""
                    plugins {
                        id 'java'
                    }
                """);
    }


    @Test
    public void testGroovyJava() {
        final var builder = new PluginsBuilder();
        builder.java();
        final var actual = builder.build(BuildscriptLanguage.GROOVY,4);
        //language=groovy
        assertThat(actual).isEqualTo("""
                    plugins {
                        id 'java'
                    }
                """);
    }


    @Test
    public void testKotlinJava(){
        final var builder = new PluginsBuilder();
        builder.java();
        final var actual = builder.build(BuildscriptLanguage.KOTLIN,0);
        //language=groovy
        assertThat(actual).isEqualTo("""
                plugins {
                    id("java")
                }
                """);
    }

    @Test
    public void testKotlinIdWithVersion(){
        final var builder = new PluginsBuilder();
        builder.id("fake.id").version("0.0.0");
        final var actual = builder.build(BuildscriptLanguage.KOTLIN,0);
        //language=groovy
        assertThat(actual).isEqualTo("""
                plugins {
                    id("fake.id") version ("0.0.0")
                }
                """);
    }
}
