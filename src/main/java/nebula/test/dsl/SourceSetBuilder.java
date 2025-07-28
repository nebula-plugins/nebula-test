package nebula.test.dsl;

import java.io.File;
import java.io.IOException;
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
    void java(String file, String contents) {
        final Path pathToSourceFile = sourcesDir.toPath().resolve("java").resolve(file);
        pathToSourceFile.getParent().toFile().mkdirs();
        try {
            pathToSourceFile.toFile().createNewFile();
            Files.writeString(pathToSourceFile, contents);
        } catch (IOException e) {
            final var message = "Error writing java file to " + file;
            throw new RuntimeException(message, e);
        }
    }
}
