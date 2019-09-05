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
        def example2 = new Pom('test.nebula', 'ourbom', '0.2.0', ArtifactType.POM)
        repo.poms.add(example)
        repo.poms.add(example2)

        when:
        repo.generate()

        then:
        def pom = new File("${rootDir}/test/nebula/ourbom/0.1.0/ourbom-0.1.0.pom")
        pom.exists()

        def metadataFile = new File("${rootDir}/test/nebula/ourbom/maven-metadata.xml")
        metadataFile.exists()

        def metadata = new XmlSlurper().parse(metadataFile)
        metadata.groupId == 'test.nebula'
        metadata.artifactId == 'ourbom'
        metadata.versioning.latest == '0.2.0'
        metadata.versioning.release == '0.2.0'
        metadata.versioning.versions.children().toSet().collect { it.toString() }.containsAll(['0.1.0', '0.2.0'])
    }
}
