package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

@NullMarked
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
    @NebulaTestKitDsl
    public void java(String file, String contents) {
        language("java", file, contents);
    }

    /**
     * write a java file
     *
     * @param file     the relative path to the java file
     * @param contents the contents of the file. uses a supplier for more ideomatic DSL usage in groovy and kotlin
     */
    @NebulaTestKitDsl
    public void java(String file, Supplier<String> contents) {
        language("java", file, contents.get());
    }

    /**
     * write a groovy file
     *
     * @param file     the relative path to the groovy file
     * @param contents the contents of the file
     */
    @NebulaTestKitDsl
    public void groovy(String file, String contents) {
        language("groovy", file, contents);
    }

    /**
     * write a groovy file
     *
     * @param file     the relative path to the groovy file
     * @param contents the contents of the file. uses a supplier for more ideomatic DSL usage in groovy and kotlin
     */
    @NebulaTestKitDsl
    public void groovy(String file, Supplier<String> contents) {
        language("groovy", file, contents.get());
    }

    /**
     * write a kotlin file
     *
     * @param file     the relative path to the kotlin file
     * @param contents the contents of the file
     */
    public void kotlin(String file, String contents) {
        language("kotlin", file, contents);
    }

    /**
     * write a kotlin file
     *
     * @param file     the relative path to the kotlin file
     * @param contents the contents of the file. uses a supplier for more ideomatic DSL usage in groovy and kotlin
     */
    @NebulaTestKitDsl
    public void kotlin(String file, Supplier<String> contents) {
        language("kotlin", file, contents.get());
    }

    /**
     * write a file for specified language
     *
     * @param file     the relative path to the java file
     * @param contents the contents of the file. uses a supplier for more ideomatic DSL usage in groovy and kotlin
     */
    @NebulaTestKitDsl
    public void language(String language, String file, Supplier<String> contents) {
        language(language, file, contents.get());
    }

    /**
     * write a file for specified language
     *
     * @param file     the relative path to the file
     * @param contents the contents of the file
     */
    public void language(String language, String file, String contents) {
        final Path pathToSourceFile = sourcesDir.toPath().resolve(language).resolve(file);
        pathToSourceFile.getParent().toFile().mkdirs();
        try {
            Files.write(pathToSourceFile, contents.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            final String message = "Error writing java file to " + file;
            throw new RuntimeException(message, e);
        }
    }
}
