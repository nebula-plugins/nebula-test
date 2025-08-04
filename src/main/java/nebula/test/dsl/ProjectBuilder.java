package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NullMarked
public class ProjectBuilder {
    private final File projectDir;
    private final PluginsBuilder plugins = new PluginsBuilder();
    private final SourcesBuilder sources;
    private final List<String> dependencies = new ArrayList<>();
    @Nullable
    private Integer javaToolchain = null;
    @Nullable
    private String rawBuildScript;

    ProjectBuilder(File projectDir) {
        this.projectDir = projectDir;
        sources = new SourcesBuilder(projectDir.toPath().resolve("src").toFile());
    }

    public PluginsBuilder plugins() {
        return plugins;
    }

    public void javaToolchain(Integer javaToolchain) {
        this.javaToolchain = javaToolchain;
    }

    public void dependencies(String... dependencies) {
        this.dependencies.addAll(Arrays.asList(dependencies));
    }

    public void rawBuildScript(String buildScript) {
        rawBuildScript = buildScript;
    }

    public SourcesBuilder src() {
        return sources;
    }

    void build() {
        StringBuilder buildFileText = new StringBuilder();
        buildFileText.append(plugins.build());
        if (!dependencies.isEmpty()) {
            buildFileText.append("dependencies {\n");
            dependencies.forEach(dependency -> buildFileText.append("    ").append(dependency).append("\n"));
            buildFileText.append("}\n");
        }
        if (javaToolchain != null) {
            buildFileText.append("java {\n    toolchain {\n");
            buildFileText.append("        languageVersion = JavaLanguageVersion.of(").append(javaToolchain).append(")\n");
            buildFileText.append("    }\n}\n");
        }
        if (rawBuildScript != null) {
            buildFileText.append(rawBuildScript);
        }
        final var buildFile = projectDir.toPath().resolve("build.gradle.kts");
        try {
            buildFile.toFile().createNewFile();
            Files.writeString(buildFile, buildFileText.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error writing to " + buildFile.toAbsolutePath(), e);
        }
    }
}
