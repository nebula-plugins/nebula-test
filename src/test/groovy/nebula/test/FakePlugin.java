package nebula.test;


import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class FakePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Intentionally empty
    }
}