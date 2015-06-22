package nebula.test.functional.internal.classpath

import com.google.common.base.Function
import com.google.common.base.Predicate
import com.google.common.collect.FluentIterable
import groovy.transform.CompileStatic
import org.gradle.internal.ErroringAction
import org.gradle.internal.IoActions
import org.gradle.internal.classloader.ClasspathUtil
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
                writer.write("allprojects {\n");
                writer.write("  buildscript {\n");
                writer.write("    dependencies {\n");
                for (File file : classpath) {
                    // Commons-lang 2.4 does not escape forward slashes correctly, https://issues.apache.org/jira/browse/LANG-421
                    writer.write(String.format("      classpath files('%s')\n", TextUtil.escapeString(file.getAbsolutePath())));
                }
                writer.write("    }\n");
                writer.write("  }\n");
                writer.write("}\n");
            }
        });
    }

    public static List<File> getClasspathAsFiles(ClassLoader classLoader, Predicate<URL> classpathFilter) {
        List<URL> classpathUrls = ClasspathUtil.getClasspath(classLoader);
        return FluentIterable.from(classpathUrls).filter(classpathFilter).transform(new Function<URL, File>() {
            @Override
            File apply(URL url) {
                return new File(url.toURI());
            }
        }).toList()
    }
}
