package nebula.test.dsl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RepositoriesBuilderTest {

    @Test
    public void testGroovyMaven(){
        final var builder = new RepositoriesBuilder();
        builder.maven("url");
        final var actual = builder.build(BuildscriptLanguage.GROOVY,0);
        //language=groovy
        assertThat(actual).isEqualTo("""
                repositories {
                    maven {
                        url = 'url'
                    }
                }
                """);
    }

    @Test
    public void testGroovyMavenIndent(){
        final var builder = new RepositoriesBuilder();
        builder.maven("url");
        final var actual = builder.build(BuildscriptLanguage.GROOVY,4);
        //language=groovy
        assertThat(actual).isEqualTo("""
                    repositories {
                        maven {
                            url = 'url'
                        }
                    }
                """);
    }

    @Test
    public void testGroovyMavenCentral(){
        final var builder = new RepositoriesBuilder();
        builder.mavenCentral();
        final var actual = builder.build(BuildscriptLanguage.GROOVY,0);
        //language=groovy
        assertThat(actual).isEqualTo("""
                repositories {
                    mavenCentral()
                }
                """);
    }

    @Test
    public void testKotlinMaven(){
        final var builder = new RepositoriesBuilder();
        builder.maven("url");
        final var actual = builder.build(BuildscriptLanguage.KOTLIN,0);
        //language=kotlin
        assertThat(actual).isEqualTo("""
                repositories {
                    maven(url = "url")
                }
                """);
    }
}
