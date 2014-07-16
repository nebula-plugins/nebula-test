package nebula.test

import spock.lang.Shared

class CustomCleanupProjectSpec extends ProjectSpec {
    @Shared
    File refProjectDir

    def setup() {
        refProjectDir = projectDir
    }

    def cleanupSpec() {
        assert refProjectDir.exists()
    }

    @Override
    boolean deleteProjectDir() {
        false
    }

    def "Avoids cleaning project directory after test"() {
        expect:
        project != null
        projectDir.exists()
    }
}