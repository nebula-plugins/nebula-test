package nebula.test

import java.util.concurrent.atomic.AtomicBoolean

class ConcreteProjectSpec extends ProjectSpec {
    def 'has Project'() {
        expect:
        project != null
    }

    def 'can evaluate'() {
        setup:
        def signal = new AtomicBoolean(false)
        project.afterEvaluate {
            signal.getAndSet(true)
        }
        when:
        project.evaluate()

        then:
        noExceptionThrown()
        signal.get() == true
    }
}