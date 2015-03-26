package nebula.test

import org.gradle.api.internal.project.ProjectInternal

/**
 * A {@link ProjectSpec} that delegates to the project under test.
 */
public class ProjectDelegateSpec extends AbstractProjectSpec {
    @Delegate ProjectInternal delegate

    @Override
    def setup() {
        delegate = super.project
    }
}
