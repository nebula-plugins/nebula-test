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
    private final RepositoriesBuilder repositoriesBuilder = new RepositoriesBuilder();
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

    @NebulaTestKitDsl
    public PluginsBuilder plugins() {
        return plugins;
    }

    @NebulaTestKitDsl
    public RepositoriesBuilder repositories() {
        return repositoriesBuilder;
    }

    @NebulaTestKitDsl
    public void javaToolchain(Integer javaToolchain) {
        this.javaToolchain = javaToolchain;
    }

    @NebulaTestKitDsl
    public void dependencies(String... dependencies) {
        this.dependencies.addAll(Arrays.asList(dependencies));
    }

    public void rawBuildScript(String buildScript) {
        rawBuildScript = buildScript;
    }

    @NebulaTestKitDsl
    public SourcesBuilder src() {
        return sources;
    }

    void build(BuildscriptLanguage language) {
        StringBuilder buildFileText = new StringBuilder();
        buildFileText.append(plugins.build(language, 0));
        buildFileText.append(repositoriesBuilder.build(language, 0));
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
        final var ext = language == BuildscriptLanguage.GROOVY ? "gradle" : "gradle.kts";
        final var buildFile = projectDir.toPath().resolve("build." + ext);
        try {
            buildFile.toFile().createNewFile();
            Files.writeString(buildFile, buildFileText.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error writing to " + buildFile.toAbsolutePath(), e);
        }
    }
}
