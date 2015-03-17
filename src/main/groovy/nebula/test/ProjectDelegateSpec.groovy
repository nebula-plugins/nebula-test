package nebula.test

import org.gradle.api.internal.project.ProjectInternal

/**
 * A {@link ProjectSpec} that delegates to the project under test.
 */
public class ProjectDelegateSpec extends ProjectSpec {
    @Delegate ProjectInternal delegate

    def setup() {
        delegate = project
    }
}
