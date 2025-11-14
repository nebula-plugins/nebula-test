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
                    java
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

    @Test
    public void testKotlinJvm_kotlin(){
        final var builder = new PluginsBuilder();
        builder.kotlin("jvm").version("0.0.0");
        final var actual = builder.build(BuildscriptLanguage.KOTLIN,0);
        //language=kotlin
        assertThat(actual).isEqualTo("""
                plugins {
                    kotlin("jvm") version ("0.0.0")
                }
                """);
    }


    @Test
    public void testKotlinJvm_groovy(){
        final var builder = new PluginsBuilder();
        builder.kotlin("jvm").version("0.0.0");
        final var actual = builder.build(BuildscriptLanguage.GROOVY,0);
        //language=groovy
        assertThat(actual).isEqualTo("""
                plugins {
                    id 'org.jetbrains.kotlin.jvm' version '0.0.0'
                }
                """);
    }

    @Test
    public void testKotlinDsl_kotlin(){
        final var builder = new PluginsBuilder();
        builder.kotlinDsl();
        final var actual = builder.build(BuildscriptLanguage.KOTLIN,0);
        //language=kotlin
        assertThat(actual).isEqualTo("""
                plugins {
                    `kotlin-dsl`
                }
                """);
    }

    @Test
    public void testKotlinDsl_groovy(){
        final var builder = new PluginsBuilder();
        builder.kotlinDsl().version("0.0.0");
        final var actual = builder.build(BuildscriptLanguage.GROOVY,0);
        //language=groovy
        assertThat(actual).isEqualTo("""
                plugins {
                    id 'org.gradle.kotlin.kotlin-dsl' version '0.0.0'
                }
                """);
    }

}
