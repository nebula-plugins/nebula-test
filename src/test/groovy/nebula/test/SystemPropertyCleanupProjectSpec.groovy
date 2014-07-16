package nebula.test

import spock.lang.Shared

class SystemPropertyCleanupProjectSpec extends ProjectSpec {
    @Shared
    File refProjectDir

    def setup() {
        refProjectDir = projectDir
    }

    def cleanupSpec() {
        assert !refProjectDir.exists()
    }

    def "Cleans project directory after test"() {
        setup:
        System.setProperty('CLEAN_PROJECT_DIR_SYS_PROP', 'true')

        expect:
        project != null
        projectDir.exists()

        cleanup:
        System.clearProperty('CLEAN_PROJECT_DIR_SYS_PROP')
    }
}