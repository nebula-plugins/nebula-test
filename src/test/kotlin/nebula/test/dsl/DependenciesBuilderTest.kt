package nebula.test.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DependenciesBuilderTest {

    @Test
    fun test() {
        val actual = DependenciesBuilder().apply {
            api("group:a")
            implementation("group:b")
            add("archRules", "group:c")
        }.build(BuildscriptLanguage.KOTLIN, 0)
        assertThat(actual).isEqualTo(
            """dependencies {
    api("group:a")
    implementation("group:b")
    archRules("group:c")
}
"""
        )
    }

    @Test
    fun `test project`() {
        val actual = DependenciesBuilder().apply {
            implementation(project(":projecta"))
        }.build(BuildscriptLanguage.KOTLIN, 0)
        assertThat(actual).isEqualTo(
            """dependencies {
    implementation(project(":projecta"))
}
"""
        )
    }
}