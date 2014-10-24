package nebula.test.functional

import nebula.test.functional.internal.GradleHandle
import nebula.test.functional.internal.GradleHandleFactory
import nebula.test.functional.internal.toolingapi.ToolingApiGradleHandleFactory
import spock.lang.Specification

class ToolingApiGradleHandleFactorySpec extends Specification {
    File projectDir = new File('myProject')

    def "Creates embedded handle if requested through constructor"() {
        when:
        GradleHandleFactory gradleHandleFactory = new ToolingApiGradleHandleFactory(false, null)
        GradleHandle gradleHandle = gradleHandleFactory.start(projectDir, [])

        then:
        gradleHandle
        !gradleHandle.forkedProcess
    }

    def "Creates forked handle if requested through constructor"() {
        when:
        GradleHandleFactory gradleHandleFactory = new ToolingApiGradleHandleFactory(true, null)
        GradleHandle gradleHandle = gradleHandleFactory.start(projectDir, [])

        then:
        gradleHandle
        gradleHandle.forkedProcess
    }

    def "Creates forked handle if requested through system property"() {
        setup:
        System.setProperty(ToolingApiGradleHandleFactory.FORK_SYS_PROP, Boolean.TRUE.toString())

        when:
        GradleHandleFactory gradleHandleFactory = new ToolingApiGradleHandleFactory(false, null)
        GradleHandle gradleHandle = gradleHandleFactory.start(projectDir, [])

        then:
        gradleHandle
        gradleHandle.forkedProcess

        cleanup:
        System.clearProperty(ToolingApiGradleHandleFactory.FORK_SYS_PROP)
    }
}
