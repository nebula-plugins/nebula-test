package nebula.test

import org.gradle.api.Plugin
import org.gradle.api.Project

class ConcretePluginProjectSpec extends PluginProjectSpec {
    @Override
    String getPluginName() {
        'fake-plugin'
    }
}

private class FakePlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // Intentionally empty
    }
}
