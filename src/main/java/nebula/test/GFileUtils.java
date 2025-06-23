package nebula.test;


import org.gradle.api.UncheckedIOException;
import org.gradle.internal.IoActions;
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils;
import org.gradle.internal.impldep.org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class GFileUtils {

    /**
     * Ensures that the given file (or directory) is marked as modified. If the file
     * (or directory) does not exist, a new file is created.
     */
    public static void touch(File file) {
        try {
            if (!file.createNewFile()) {
                touchExistingInternal(file);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Ensures that the given file (or directory) is marked as modified. The file
     * (or directory) must exist.
     */
    public static void touchExisting(File file) {
        touchExistingInternal(file);
    }

    private static void touchExistingInternal(File file) {
        try {
            Files.setLastModifiedTime(file.toPath(), FileTime.fromMillis(System.currentTimeMillis()));
        } catch (IOException e) {
            if (file.isFile() && file.length() == 0) {
                // On Linux, users cannot touch files they don't own but have write access to
                // because the JDK uses futimes() instead of futimens() [note the 'n'!]
                // see https://github.com/gradle/gradle/issues/7873
                touchFileByWritingEmptyByteArray(file);
            } else {
                throw new UncheckedIOException("Could not update timestamp for " + file, e);
            }
        }
    }

    private static void touchFileByWritingEmptyByteArray(File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(ArrayUtils.EMPTY_BYTE_ARRAY);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not update timestamp for " + file, e);
        } finally {
            IoActions.closeQuietly(out);
        }
    }


    public static void copyFile(File source, File destination) {
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void copyDirectory(File source, File destination) {
        try {
            FileUtils.copyDirectory(source, destination);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void moveDirectory(File source, File destination) {
        moveDirectoryInternal(source, destination);
    }

    private static void moveDirectoryInternal(File source, File destination) {
        try {
            FileUtils.moveDirectory(source, destination);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public static String readFile(File file) {
        return readFile(file, Charset.defaultCharset().name());
    }

    public static String readFile(File file, String encoding) {
        try {
            return FileUtils.readFileToString(file, encoding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reads and returns file contents. If some exception is triggered the method returns information about this exception.
     * Useful for including file contents in debug trace / exception messages.
     *
     * @param file to read
     * @return content of the file or the problem description in case file cannot be read.
     */
    public static String readFileQuietly(File file) {
        try {
            return readFile(file);
        } catch (Exception e) {
            return "Unable to read file '" + file + "' due to: " + e.toString();
        }
    }

    public static void writeFile(String content, File destination) {
        writeFile(content, destination, Charset.defaultCharset().name());
    }

    public static void writeFile(String content, File destination, String encoding) {
        try {
            FileUtils.writeStringToFile(destination, content, encoding);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }



    /**
     * Like {@link java.io.File#mkdirs()}, except throws an informative error if a dir cannot be created.
     *
     * @param dir The dir to create, including any non existent parent dirs.
     */
    public static void mkdirs(File dir) {
        dir = dir.getAbsoluteFile();
        if (dir.isDirectory()) {
            return;
        }

        if (dir.exists() && !dir.isDirectory()) {
            throw new UncheckedIOException(String.format("Cannot create directory '%s' as it already exists, but is not a directory", dir));
        }

        List<File> toCreate = new LinkedList<File>();
        File parent = dir.getParentFile();
        while (!parent.exists()) {
            toCreate.add(parent);
            parent = parent.getParentFile();
        }

        Collections.reverse(toCreate);
        for (File parentDirToCreate : toCreate) {
            if (parentDirToCreate.isDirectory()) {
                continue;
            }

            File parentDirToCreateParent = parentDirToCreate.getParentFile();
            if (!parentDirToCreateParent.isDirectory()) {
                throw new UncheckedIOException(String.format("Cannot create parent directory '%s' when creating directory '%s' as '%s' is not a directory", parentDirToCreate, dir, parentDirToCreateParent));
            }

            if (!parentDirToCreate.mkdir() && !parentDirToCreate.isDirectory()) {
                throw new UncheckedIOException(String.format("Failed to create parent directory '%s' when creating directory '%s'", parentDirToCreate, dir));
            }
        }

        if (!dir.mkdir() && !dir.isDirectory()) {
            throw new UncheckedIOException(String.format("Failed to create directory '%s'", dir));
        }
    }


}