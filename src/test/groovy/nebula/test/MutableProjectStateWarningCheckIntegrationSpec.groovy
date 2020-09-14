package nebula.test

import spock.lang.IgnoreIf

@IgnoreIf({ System.getenv('TITUS_TASK_ID') })
class MutableProjectStateWarningCheckIntegrationSpec extends IntegrationSpec {

    def setup() {
        gradleVersion = "5.1"
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
        runTasks("resolve", "--parallel", "--warning-mode", "all")

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains('Mutable Project State warnings were found (Set the ignoreMutableProjectStateWarnings system property during the test to ignore)')
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
        runTasks("resolve", "--warning-mode", "all")

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains('Mutable Project State warnings were found (Set the ignoreMutableProjectStateWarnings system property during the test to ignore)')
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
        runTasks(":bar:help", "--parallel", "--warning-mode", "all")

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains('Mutable Project State warnings were found (Set the ignoreMutableProjectStateWarnings system property during the test to ignore)')
    }

}
