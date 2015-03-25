package nebula.test

import java.util.concurrent.atomic.AtomicBoolean

class ConcreteProjectDelegateSpec extends ProjectDelegateSpec {
    def 'has Project'() {
        expect:
        project != null
        delegate != null
        name == canonicalName
    }

    def 'can evaluate'() {
        setup:
        def signal = new AtomicBoolean(false)
        afterEvaluate {
            signal.getAndSet(true)
        }
        when:
        evaluate()

        then:
        noExceptionThrown()
        signal.get()
    }

}
