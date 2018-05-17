package nebula.test.functional.internal.classpath

import com.google.common.base.Function
import com.google.common.base.Predicate
import com.google.common.collect.FluentIterable
import groovy.transform.CompileStatic
import org.gradle.internal.ErroringAction
import org.gradle.internal.IoActions
import org.gradle.internal.classloader.ClasspathUtil
import org.gradle.internal.classpath.ClassPath
import org.gradle.util.TextUtil

@CompileStatic
class ClasspathAddingInitScriptBuilder {
    private ClasspathAddingInitScriptBuilder() {
    }

    public static void build(File initScriptFile, final ClassLoader classLoader, Predicate<URL> classpathFilter) {
        build(initScriptFile, getClasspathAsFiles(classLoader, classpathFilter));
    }

    public static void build(File initScriptFile, final List<File> classpath) {
        IoActions.writeTextFile(initScriptFile, new ErroringAction<Writer>() {
            @Override
            protected void doExecute(Writer writer) throws Exception {
                writer.write("allprojects {\n")
                writer.write("  buildscript {\n")
                writer.write("    dependencies {\n")
                writeClasspath(writer, classpath)
                writer.write("    }\n")
                writer.write("  }\n")
                writer.write("}\n")
                writer.write("initscript {\n")
                writer.write("  dependencies {\n")
                writeClasspath(writer, classpath)
                writer.write("  }\n")
                writer.write("}\n")
            }
        })
    }

    public static writeClasspath(Writer writer, List<File> classpath) {
        for (File file : classpath) {
            // Commons-lang 2.4 does not escape forward slashes correctly, https://issues.apache.org/jira/browse/LANG-421
            writer.write(String.format("      classpath files('%s')\n", TextUtil.escapeString(file.getAbsolutePath())));
        }
    }

    public static List<File> getClasspathAsFiles(ClassLoader classLoader, Predicate<URL> classpathFilter) {
        List<URL> classpathUrls = getClasspathUrls(classLoader)
        return FluentIterable.from(classpathUrls).filter(classpathFilter).transform(new Function<URL, File>() {
            @Override
            File apply(URL url) {
                return new File(url.toURI());
            }
        }).toList()
    }

    private static List<URL> getClasspathUrls(ClassLoader classLoader) {
        Object cp = ClasspathUtil.getClasspath(classLoader)
        if (cp instanceof List<URL>) {
            return (List<URL>) cp
        }
        if (cp instanceof ClassPath) { // introduced by gradle/gradle@0ab8bc2
            return ((ClassPath) cp).asURLs
        }
        throw new IllegalStateException("Unable to extract classpath urls from type ${cp.class.canonicalName}")
    }
}
