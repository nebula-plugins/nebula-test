package nebula.test.dsl

import nebula.test.SupportedGradleVersion
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import spock.lang.Retry

import static nebula.test.dsl.TestKitAssertions.assertThat

class GroovyDslTestKitTest {
    @TempDir
    File testProjectDir

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion)
    @Retry(count = 2)
    void "groovy DSL testkit integration test"(SupportedGradleVersion gradle) {
        final var runner = GroovyTestProjectBuilder.testProject(testProjectDir) {
            settings {
                pluginManagement {
                    repositories {

                    }
                    plugins {

                    }
                }
                plugins {

                }
            }
            rootProject {
                buildscript {
                    repositories {

                    }
                    dependencies {

                    }
                }
                plugins {

                }
            }
            subProject("sub"){
                plugins {
                    java()
                }
                repositories {
                    mavenCentral()
                }
                dependencies {
                    implementation("org.jspecify:jspecify:1.0.0")
                }
                src {
                    main {
                        java("Main.java") {
                            // language=java
                            """
public class Main {
    public static void main(String[] args) {
    }
}
"""
                        }
                    }
                }
            }
        }

        final var result = runner.run(["build"]) {
            withGradle(gradle.version)
            forwardOutput()
        }

        assertThat(result)
                .hasNoDeprecationWarnings()
                .hasNoMutableStateWarnings()
        assertThat(result).task(":sub:compileJava").hasOutcome(TaskOutcome.SUCCESS)
        assertThat(result).task(":sub:build").hasOutcome(TaskOutcome.SUCCESS)
    }

    @ParameterizedTest
    @EnumSource(SupportedGradleVersion)
    @Retry(count = 2)
    void "test test suites"(SupportedGradleVersion gradleVersion) {
        TestProjectRunner runner = GroovyTestProjectBuilder.testProject(testProjectDir) {
            properties {
                buildCache(true)
            }
            rootProject {
                plugins {
                    java()
                }
                repositories {
                    mavenCentral()
                }
                src {
                    main {
                        java("Main.java") {
                            // language=java
                            """
public class Main {
    public static void main(String[] args) {
    }
}
"""
                        }
                    }

                    test {
                        java("MainTest.java") {
                            // language=java
                            """
import org.junit.jupiter.api.Test;
public class MainTest {
    
    @Test
    void test() {
    }
}
"""
                        }
                    }
                    sourceSet("customTest") {
                        java("MainTest.java") {
                            // language=java
                            """
import org.junit.jupiter.api.Test;
public class MainTest {
    
    @Test
    void test() {
    }
}
"""
                        }
                    }
                }
                testing {
                    suites {
                        test {
                            useJUnitJupiter()
                        }
                        create("customTest") {
                            useJUnitJupiter()
                        }
                    }
                }
            }
        }

        BuildResult result = runner.run(["test", "customTest"]) {
            forwardOutput()
            withGradle(gradleVersion.version)
        }

        assertThat(result)
                .hasNoDeprecationWarnings()
                .hasNoMutableStateWarnings()

        assertThat(result).task(":test")
                .hasOutcome(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE)
        assertThat(result).task(":customTest")
                .hasOutcome(TaskOutcome.SUCCESS, TaskOutcome.FROM_CACHE)
    }
}
