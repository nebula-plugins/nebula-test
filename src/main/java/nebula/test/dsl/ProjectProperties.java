package nebula.test.dsl;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@NebulaTestKitDsl
public class ProjectProperties {
    private final File projectDir;

    public ProjectProperties(File projectDir) {
        this.projectDir = projectDir;
    }

    private final List<String> properties = new ArrayList<>();

    @NebulaTestKitDsl
    @Contract("_,_ -> this")
    public ProjectProperties property(String property, String value) {
        properties.add(property + "=" + value);
        return this;
    }

    @NebulaTestKitDsl
    @Contract("_ -> this")
    public ProjectProperties gradleCache(boolean enabled) {
        return property("org.gradle.caching", String.valueOf(enabled).toLowerCase());
    }

    void build() {
        Path propsFile = projectDir.toPath().resolve("gradle.properties");
        String fileContents = String.join("\n", properties);
        try {
            Files.write(propsFile, fileContents.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Error writing properties file" + propsFile.toFile().getAbsolutePath(), e);
        }
    }
}
