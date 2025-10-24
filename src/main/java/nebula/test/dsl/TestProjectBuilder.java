package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the entry point into the "test project builder" API.
 * Although it may be used form Java, it is most effective when used via the Groovy or Kotlin DSLs.
 * See {@link GroovyTestProjectBuilder} and {@link KotlinTestProjectBuilderKt} for more information on the DSLs.
 */
@NullMarked
public class TestProjectBuilder {

    private final ProjectBuilder rootProject;
    private final Map<String, ProjectBuilder> subProjects = new HashMap<>();
    private final File projectDir;
    private final SettingsBuilder settings;
    private final ProjectProperties properties;

    private TestProjectBuilder(File projectDir) {
        this.projectDir = projectDir;
        rootProject = new ProjectBuilder(projectDir);
        settings = new SettingsBuilder(projectDir);
        properties = new ProjectProperties(projectDir);
    }

    public static TestProjectBuilder testProject(File testProjectDir) {
        return new TestProjectBuilder(testProjectDir);
    }

    /**
     * configure the root project of the build
     *
     * @return builder for the root project
     */
    public ProjectBuilder rootProject() {
        return rootProject;
    }

    public ProjectProperties properties() {
        return properties;
    }

    /**
     * configure the settings (settings.gradle.kts)
     *
     * @return builder for the settings
     */
    public SettingsBuilder settings() {
        return settings;
    }

    public ProjectBuilder subProject(String name) {
        final File subProjectDir = projectDir.toPath().resolve(name).toFile();
        subProjectDir.mkdirs();
        final ProjectBuilder project = new ProjectBuilder(subProjectDir);
        subProjects.put(name, project);
        settings.includeProject(name);
        return project;
    }

    public TestProjectRunner build() {
        return build(BuildscriptLanguage.KOTLIN);
    }

    public TestProjectRunner build(BuildscriptLanguage language) {
        properties().build();
        settings.build(language);
        rootProject.build(language);
        subProjects.values().forEach(subProject -> subProject.build(language));
        return new TestProjectRunner(projectDir);
    }
}
