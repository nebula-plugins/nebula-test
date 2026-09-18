package nebula.test.dsl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PluginTest {

    @Test
    public void test_id_java() {
        String actual = new Plugin("java").render(BuildscriptLanguage.GROOVY);
        assertThat(actual).isEqualTo("id 'java'");
    }

    @Test
    public void test_kotlin_apply_false_kotlin() {
        String actual = new Plugin("org.jetbrains.kotlin.jvm")
                .builtIn("kotlin")
                .builtInParam("jvm")
                .apply(false)
                .render(BuildscriptLanguage.KOTLIN);
        assertThat(actual).isEqualTo("kotlin(\"jvm\") apply (false)");
    }

    @Test
    public void test_kotlin_version_apply_false_kotlin() {
        String actual = new Plugin("org.jetbrains.kotlin.jvm")
                .builtIn("kotlin")
                .builtInParam("jvm")
                .version("2.4.10")
                .apply(false)
                .render(BuildscriptLanguage.KOTLIN);
        assertThat(actual).isEqualTo("kotlin(\"jvm\") version (\"2.4.10\") apply (false)");
    }

    @Test
    public void test_kotlin_apply_false_groovy() {
        String actual = new Plugin("org.jetbrains.kotlin.jvm")
                .builtIn("kotlin")
                .builtInParam("jvm")
                .apply(false)
                .render(BuildscriptLanguage.GROOVY);
        assertThat(actual).isEqualTo("id 'org.jetbrains.kotlin.jvm' apply false");
    }
}
