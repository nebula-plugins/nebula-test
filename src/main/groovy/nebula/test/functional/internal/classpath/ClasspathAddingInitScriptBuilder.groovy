package nebula.test.functional.internal.classpath

import groovy.transform.CompileStatic
import org.gradle.api.Transformer;
import org.gradle.internal.ErroringAction;
import org.gradle.internal.IoActions;
import org.gradle.internal.UncheckedException;
import org.gradle.internal.classloader.ClasspathUtil;
import org.gradle.util.CollectionUtils
import org.gradle.util.TextUtil;

@CompileStatic
class ClasspathAddingInitScriptBuilder {

    public void build(File initScriptFile, final ClassLoader classLoader) {
        build(initScriptFile, getClasspathAsFiles(classLoader));
    }

    public void build(File initScriptFile, final List<File> classpath) {
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

    public List<File> getClasspathAsFiles(ClassLoader classLoader) {
        List<URL> classpathUrls = ClasspathUtil.getClasspath(classLoader);
        return CollectionUtils.collect(classpathUrls, new ArrayList<File>(classpathUrls.size()), new Transformer<File, URL>() {
            public File transform(URL url) {
                try {
                    return new File(url.toURI());
                } catch (URISyntaxException e) {
                    throw UncheckedException.throwAsUncheckedException(e);
                }
            }
        });
    }

}
