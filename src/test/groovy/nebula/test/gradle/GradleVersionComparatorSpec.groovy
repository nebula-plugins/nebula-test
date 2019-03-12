package nebula.test.gradle

import spock.lang.Specification
import spock.lang.Subject

@Subject(GradleVersionComparator)
class GradleVersionComparatorSpec extends Specification {

    def 'checks if version is greater than'() {
        given:
        String version = '5.0'
        Boolean result

        when:
        use(GradleVersionComparator) {
            result = version.versionGreaterThan('4.10.3')
        }

        then:
        result
    }

    def 'checks if version is less than'() {
        given:
        String version = '5.0'
        Boolean result

        when:
        use(GradleVersionComparator) {
            result = version.versionLessThan('5.1')
        }

        then:
        result
    }
}
