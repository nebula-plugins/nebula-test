package nebula.test.dsl

/**
 * Entry point for the Groovy TestKit DSL project builder
 */
class GroovyTestProjectBuilder {
    static TestProjectRunner testProject(File testProjectDir, @DelegatesTo(TestProjectBuilder) Closure config) {
        final TestProjectBuilder testProjectBuilder = new TestProjectBuilder(testProjectDir)
        testProjectBuilder.with(config)
        return testProjectBuilder.build()
    }
}
