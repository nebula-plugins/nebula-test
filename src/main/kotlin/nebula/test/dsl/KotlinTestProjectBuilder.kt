package nebula.test.dsl

import java.io.File

/**
 * Entry point for the Kotlin DSL for [TestProjectBuilder].
 * @param
 */
@NebulaTestKitDsl
fun testProject(testProjectDir: File, config: TestProjectBuilder.() -> Unit): TestProjectRunner {
    val testProjectBuilder = TestProjectBuilder(testProjectDir)
    testProjectBuilder.apply(config)
    return testProjectBuilder.build()
}
