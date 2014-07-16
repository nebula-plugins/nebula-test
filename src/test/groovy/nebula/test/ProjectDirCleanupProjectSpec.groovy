package nebula.test

import spock.lang.Shared

class ProjectDirCleanupProjectSpec extends ProjectSpec {
    @Shared
    File refProjectDir

    def setup() {
        refProjectDir = projectDir
    }

    def cleanupSpec() {
        assert !refProjectDir.exists()
    }

    def "Cleans project directory after test"() {
        expect:
        project != null
        projectDir.exists()
    }
}
