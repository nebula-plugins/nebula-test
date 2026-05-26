package nebula.test.dsl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestingSuitesBuilderTest {

    @Test
    public void test_create_custom_suite_groovy() {
        TestingSuitesBuilder instance = new TestingSuitesBuilder();
        instance.create("custom");
        String actual = instance.build(BuildscriptLanguage.GROOVY,0);
        assertThat(actual).isEqualTo(
                //language=groovy
"""
suites {
    custom(JvmTestSuite) {
    }
}
""");
    }
}
