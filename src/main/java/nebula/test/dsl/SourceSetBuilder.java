package nebula.test.dsl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SourceSetBuilder {
    private final File sourcesDir;

    SourceSetBuilder(File sourcesDir) {
        this.sourcesDir = sourcesDir;
    }

    /**
     * write a java file
     *
     * @param file     the relative path to the java file
     * @param contents the contents of the file
     */
    public void java(String file, String contents) {
        final Path pathToSourceFile = sourcesDir.toPath().resolve("java").resolve(file);
        pathToSourceFile.getParent().toFile().mkdirs();
        try {
            Files.write(pathToSourceFile, contents.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            final String message = "Error writing java file to " + file;
            throw new RuntimeException(message, e);
        }
    }
}
