package nebula.test.dependencies.repositories

import nebula.test.dependencies.maven.ArtifactType
import nebula.test.dependencies.maven.Pom
import spock.lang.Specification

class MavenRepoSpec extends Specification {
    def 'create repo'() {
        def repo = new MavenRepo()
        final String rootDir = 'build/test/nebula.test.dependencies.repositories.MavenRepoSpec/create_repo/mavenrepo'
        repo.root = new File(rootDir)
        if (repo.root.exists()) {
            repo.root.deleteDir()
        }
        def example = new Pom('test.nebula', 'ourbom', '0.1.0', ArtifactType.POM)
        repo.poms.add(example)

        when:
        repo.generate()

        then:
        def pom = new File("${rootDir}/test/nebula/ourbom/0.1.0/ourbom-0.1.0.pom")
        pom.exists()
    }
}
