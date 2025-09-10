package nebula.test.dsl;

import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class SettingsBuilder {
    private final File projectDir;
    private final PluginManagementBuilder pluginManagement = new PluginManagementBuilder();
    private final PluginsBuilder plugins = new PluginsBuilder();
    @Nullable
    private String rawSettingsScript;
    @Nullable
    private String name;
    private final Set<String> projects = new HashSet<>();

    SettingsBuilder(File projectDir) {
        this.projectDir = projectDir;
    }

    public void name(String name) {
        this.name = name;
    }

    public PluginManagementBuilder pluginManagement() {
        return pluginManagement;
    }

    public PluginsBuilder plugins() {
        return plugins;
    }

    /**
     * add a project include statement.
     * This is invoked automatically when adding projects via {@link TestProjectBuilder#subProject(String)}
     *
     * @param name the name of the project to include
     */
    public void includeProject(String name) {
        projects.add(name);
    }

    public void rawSettingsScript(String settingsScript) {
        rawSettingsScript = settingsScript;
    }

    void build(BuildscriptLanguage language) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append(pluginManagement.build(language));
        textBuilder.append(plugins.build(language, 0));
        if (name != null) {
            textBuilder.append("rootProject.name = \"").append(name).append("\"\n");
        }
        if (rawSettingsScript != null) {
            textBuilder.append(rawSettingsScript);
        }
        if (language == BuildscriptLanguage.KOTLIN) {
            projects.forEach(name -> textBuilder.append("include(\":").append(name).append("\")\n"));
        } else if (language == BuildscriptLanguage.GROOVY) {
            projects.forEach(name -> textBuilder.append("include ':").append(name).append("'\n"));
        }
        final var ext = language == BuildscriptLanguage.GROOVY ? "gradle" : "gradle.kts";
        final var settingsFile = projectDir.toPath().resolve("settings." + ext);
        try {
            settingsFile.toFile().createNewFile();
            Files.writeString(settingsFile, textBuilder.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error writing to " + settingsFile.toAbsolutePath(), e);
        }
    }
}
