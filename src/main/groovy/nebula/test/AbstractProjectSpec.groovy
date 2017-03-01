package nebula.test

import groovy.transform.CompileStatic
import nebula.test.multiproject.MultiProjectHelper
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TestName
import spock.lang.Specification

/**
 * Setup a temporary project on the fly, uses Spock.
 *
 * Caveat, this is ONLY setting up the Project data structure, and not running through the completely lifecycle, Like to
 * see http://issues.gradle.org/browse/GRADLE-1619
 *
 * Its value lays in being able to execute method with a proper Project object, which can flush out most groovy functions,
 * finding basic compiler like issues.
 */
@CompileStatic
public abstract class AbstractProjectSpec extends Specification {
    static final String CLEAN_PROJECT_DIR_SYS_PROP = 'cleanProjectDir'
    File ourProjectDir

    @Rule TestName testName = new TestName()
    String canonicalName
    Project project
    MultiProjectHelper helper

    def setup() {
        ourProjectDir = new File("build/nebulatest/${this.class.canonicalName}/${testName.methodName.replaceAll(/\W+/, '-')}")
        if (ourProjectDir.exists()) {
            ourProjectDir.deleteDir()
        }
        ourProjectDir.mkdirs()
        canonicalName = testName.getMethodName().replaceAll(' ', '-')
        project = ProjectBuilder.builder().withName(canonicalName).withProjectDir(ourProjectDir).build()
        helper = new MultiProjectHelper(project)
    }

    def cleanup() {
        if(deleteProjectDir()) {
            ourProjectDir.deleteDir()
        }
    }

    /**
     * Determines if project directory should be deleted after a test was executed. By default the logic checks for
     * the system property "cleanProjectDir". If the system property is provided and has the value "true", the project
     * directory is deleted. If this system property is not provided, the project directory is always deleted. Test
     * classes that inherit from this class, can override the method to provide custom logic.
     *
     * @return Flag
     */
    boolean deleteProjectDir() {
        String cleanProjectDirSystemProperty = System.getProperty(CLEAN_PROJECT_DIR_SYS_PROP)
        cleanProjectDirSystemProperty ? cleanProjectDirSystemProperty.toBoolean() : true
    }

    Project addSubproject(String subprojectName) {
        helper.addSubproject(subprojectName)
    }

    Project addSubprojectWithDirectory(String subprojectName) {
        helper.addSubprojectWithDirectory(subprojectName)
    }
}

