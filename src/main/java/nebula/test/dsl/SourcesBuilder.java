package nebula.test.dsl;

import java.io.File;

/**
 * Represents the "src" directory of a project
 */
public class SourcesBuilder {
    private final File sourcesDir;

    SourcesBuilder(File sourcesDir) {
        this.sourcesDir = sourcesDir;
    }

    public SourceSetBuilder sourceSet(String name) {
        return new SourceSetBuilder(sourcesDir.toPath().resolve(name).toFile());
    }

    public SourceSetBuilder main() {
        return sourceSet("main");
    }

    public SourceSetBuilder test() {
        return sourceSet("test");
    }
}
