package nebula.test

import com.energizedwork.spock.extensions.TempDirectory
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
public abstract class ProjectSpec extends Specification {
    @TempDirectory File projectDir

    @Rule TestName name = new TestName()
    String canonicalName
    Project project

    def setup() {
        canonicalName = name.getMethodName().replaceAll(' ', '-')
        project = ProjectBuilder.builder().withName(canonicalName).withProjectDir(projectDir).build()
    }

    def cleanup() {
        // TODO Optionally not-delete directory for debugging
        new AntBuilder().delete(dir: projectDir)
    }
}

