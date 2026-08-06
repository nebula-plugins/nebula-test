package nebula.test.dsl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BuildscriptBuilderTest {

    @Test
    fun `test empty`(){
        val actual = BuildscriptBuilder().build(BuildscriptLanguage.KOTLIN,0)
        assertThat(actual).isEmpty()
    }
}