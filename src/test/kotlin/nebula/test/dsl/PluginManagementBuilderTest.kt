package nebula.test.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class PluginManagementBuilderTest {
    @Test
    fun test(){
        val actual = PluginManagementBuilder().apply {
            includeBuild("other")
        }.build(BuildscriptLanguage.KOTLIN)
        assertThat(actual).isEqualTo(
            """pluginManagement {
    includeBuild("other")
}
"""
        )
    }
}