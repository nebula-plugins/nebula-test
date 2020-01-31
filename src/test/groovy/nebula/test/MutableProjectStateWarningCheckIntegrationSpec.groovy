package nebula.test

class MutableProjectStateWarningCheckIntegrationSpec extends IntegrationSpec {

    def setup() {
        gradleVersion = "5.6.4"
    }

    def 'mutable project state warning when configuration in another project is resolved unsafely'() {
        given:
        settingsFile << """
            rootProject.name = "foo"
            include ":bar"
        """

        buildFile << """
             task resolve {
                doLast {
                    println project(':bar').configurations.bar.files
                }
            }

            
            project(':bar') {
                repositories {
                    mavenCentral()
                }
                
                configurations {
                    bar
                }
                
                dependencies {
                    bar group: 'junit', name: 'junit', version: '4.12'
                }
            }       
        """


        when:
        def failure = runTasksWithFailure("resolve", "--parallel").failure

        then:
        failure.message.contains('Deprecated Gradle features were used in this build, making it incompatible with Gradle 6.0')
    }

    def 'mutable project state warning when configuration is resolved from a non-gradle thread'() {
        given:
        settingsFile << """
            rootProject.name = "foo"
            include ":bar"
        """

        buildFile << """
             task resolve {
                def thread = new Thread({
                    println project(':bar').configurations.bar.files
                })
                doFirst {
                    thread.start()
                    thread.join()
                }
            }

            
            project(':bar') {
                repositories {
                    mavenCentral()
                }
                
                configurations {
                    bar
                }
                
                dependencies {
                    bar group: 'junit', name: 'junit', version: '4.12'
                }
            }       
        """


        when:
        def failure = runTasksWithFailure("resolve").failure

        then:
        failure.message.contains('Deprecated Gradle features were used in this build, making it incompatible with Gradle 6.0')
    }

    def 'mutable project state warning when configuration is resolved while evaluating a different project'() {
        given:
        settingsFile << """
            rootProject.name = "foo"
            include ":bar", ":baz"
        """

        buildFile << """
            project(':baz') {
                repositories {
                    mavenCentral()
                }
                
                configurations {
                    baz
                }
                
                dependencies {
                    baz group: 'junit', name: 'junit', version: '4.12'
                }
            }   
            
             project(':bar') {
                println project(':baz').configurations.baz.files
            }      
        """


        when:
        def failure = runTasksWithFailure(":bar:help", "--parallel").failure

        then:
        failure.message.contains('Deprecated Gradle features were used in this build, making it incompatible with Gradle 6.0')
    }

}
