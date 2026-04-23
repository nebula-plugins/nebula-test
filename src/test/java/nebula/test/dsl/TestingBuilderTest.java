package nebula.test.dsl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestingBuilderTest {
    @Test
    public void test_groovy() {
        TestingBuilder instance = new TestingBuilder();
        instance.suites().named("test").useJUnitJupiter();
        String actual = instance.build(BuildscriptLanguage.GROOVY, 0);
        assertThat(actual).isEqualTo("""
                testing {
                    suites {
                        test {
                            useJUnitJupiter()
                        }
                    }
                }
                """);
    }

    @Test
    public void test_kotlin() {
        TestingBuilder instance = new TestingBuilder();
        instance.suites().named("test").useJUnitJupiter();
        String actual = instance.build(BuildscriptLanguage.KOTLIN, 0);
        assertThat(actual).isEqualTo("""
                testing {
                    suites {
                        named<JvmTestSuite>("test") {
                            useJUnitJupiter()
                        }
                    }
                }
                """);
    }
}
