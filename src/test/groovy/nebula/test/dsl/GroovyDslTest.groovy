package nebula.test.dsl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.assertj.core.api.Assertions.assertThat

class GroovyDslTest {
    @TempDir
    File testProjectDir

    @Test
    void "test dependencies DSL"() {
        GroovyTestProjectBuilder.testProject(testProjectDir) {
            rootProject {
                dependencies {
                    implementation("lib")
                    implementation(project(":lib-project"))
                    implementation(platform("bom"))
                }
            }
        }
        assertThat(testProjectDir.toPath().resolve("build.gradle")).content()
                .contains('implementation("lib")')
                .contains('implementation(project(":lib-project"))')
                .contains('implementation(platform("bom"))')
    }

    @Test
    void "test plugins DSL"() {
        GroovyTestProjectBuilder.testProject(testProjectDir) {
            rootProject {
                plugins {
                    java()
                    id("org.springframework.boot") version "3.5.3"
                }
            }
        }
        assertThat(testProjectDir.toPath().resolve("build.gradle"))
                .content().contains("id 'org.springframework.boot' version '3.5.3'")
    }

    @Test
    void "test subProject DSL"() {
        GroovyTestProjectBuilder.testProject(testProjectDir) {
            subProject("sub1") {
                plugins {
                    java()
                }
            }
        }
        assertThat(testProjectDir.toPath().resolve("sub1/build.gradle"))
                .exists()
                .content().contains("id 'java'")
    }

    @Test
    void "test properties DSL"() {
        GroovyTestProjectBuilder.testProject(testProjectDir) {
            properties {
                buildCache(true)
                configurationCache(true)
                isolatedProjects(true)
                property("org.gradle.caching.debug", "true")
            }
        }
        assertThat(testProjectDir.toPath().resolve("gradle.properties"))
                .exists()
                .content()
                .contains("org.gradle.caching=true")
                .contains("org.gradle.configuration-cache=true")
                .contains("org.gradle.isolated-projects=true")
                .contains("org.gradle.caching.debug=true")
    }

}
