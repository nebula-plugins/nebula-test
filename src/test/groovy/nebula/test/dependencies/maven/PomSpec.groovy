package nebula.test.dependencies.maven

import spock.lang.Specification

class PomSpec extends Specification {
    def 'generate basic pom'() {
        def pom = new Pom('nebula.test', 'basic', '0.1.0')

        when:
        def pomXml = pom.generate()

        then:
        def expected = '''\
            <?xml version="1.0" encoding="UTF-8"?>
            <project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <modelVersion>4.0.0</modelVersion>
              <groupId>nebula.test</groupId>
              <artifactId>basic</artifactId>
              <version>0.1.0</version>
            </project>'''.stripIndent()
        pomXml == expected
    }

    def 'generate bom'() {
        def pom = new Pom('nebula.test', 'basic', '0.1.0', ArtifactType.POM)
        pom.addManagementDependency('foo', 'bar', '1.2.3')

        when:
        def pomXml = pom.generate()

        then:
        def expected = '''\
            <?xml version="1.0" encoding="UTF-8"?>
            <project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <modelVersion>4.0.0</modelVersion>
              <groupId>nebula.test</groupId>
              <artifactId>basic</artifactId>
              <version>0.1.0</version>
              <packaging>pom</packaging>
              <dependencyManagement>
                <dependency>
                  <groupId>foo</groupId>
                  <artifactId>bar</artifactId>
                  <version>1.2.3</version>
                </dependency>
              </dependencyManagement>
            </project>'''.stripIndent()
        pomXml == expected
    }

    def 'generate pom with dependency'() {
        def pom = new Pom('nebula.test', 'basic', '0.1.0')
        pom.addDependency('foo', 'bar', '1.2.3')
        pom.addDependency(new Artifact('baz', 'qux', '2.0.1'))

        when:
        def pomXml = pom.generate()

        then:
        def expected = '''\
            <?xml version="1.0" encoding="UTF-8"?>
            <project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
              <modelVersion>4.0.0</modelVersion>
              <groupId>nebula.test</groupId>
              <artifactId>basic</artifactId>
              <version>0.1.0</version>
              <dependencies>
                <dependency>
                  <groupId>baz</groupId>
                  <artifactId>qux</artifactId>
                  <version>2.0.1</version>
                </dependency>
                <dependency>
                  <groupId>foo</groupId>
                  <artifactId>bar</artifactId>
                  <version>1.2.3</version>
                </dependency>
              </dependencies>
            </project>'''.stripIndent()
        pomXml == expected
    }
}
