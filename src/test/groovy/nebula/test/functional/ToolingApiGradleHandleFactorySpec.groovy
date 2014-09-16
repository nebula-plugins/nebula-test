package nebula.test.functional

import nebula.test.functional.internal.GradleHandle
import nebula.test.functional.internal.GradleHandleFactory
import nebula.test.functional.internal.toolingapi.ToolingApiGradleHandleFactory
import spock.lang.Specification

class ToolingApiGradleHandleFactorySpec extends Specification {
    File projectDir = new File('myProject')
    GradleHandleFactory gradleHandleFactory = new ToolingApiGradleHandleFactory()

    def "Creates in-process handle by default"() {
        when:
        GradleHandle gradleHandle = gradleHandleFactory.start(projectDir, [])

        then:
        gradleHandle
        !gradleHandle.forkedProcess
    }

    def "Creates embedded handle if requested"() {
        setup:
        System.setProperty(ToolingApiGradleHandleFactory.FORK_SYS_PROP, Boolean.TRUE.toString())

        when:
        GradleHandle gradleHandle = gradleHandleFactory.start(projectDir, [])

        then:
        gradleHandle
        gradleHandle.forkedProcess

        cleanup:
        System.clearProperty(ToolingApiGradleHandleFactory.FORK_SYS_PROP)
    }
}
