package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the entry point into the "test project builder" API.
 * Although it may be used form Java, it is most effective when used via the Groovy or Kotlin DSLs.
 * See {@link GroovyTestProjectBuilder} and {@link KotlinTestProjectBuilderKt} for more information on the DSLs.
 */
@NullMarked
@NebulaTestKitDsl
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
        return subProject(name, null);
    }

    private String convertProjectNameToDefaultPath(String name) {
        String namePathConvention = name;
        if (namePathConvention.startsWith(":")) {
            namePathConvention = namePathConvention.substring(1);
        }
        namePathConvention = namePathConvention.replace(":", "/");
        return namePathConvention;
    }

    public ProjectBuilder subProject(String name, @Nullable String path) {
        String projectPath = path == null ? convertProjectNameToDefaultPath(name) : path;
        final File subProjectDir = projectDir.toPath().resolve(projectPath).toFile();
        subProjectDir.mkdirs();
        final ProjectBuilder project = new ProjectBuilder(subProjectDir);
        subProjects.put(name, project);
        settings.includeProject(name, path);
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
