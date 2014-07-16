package nebula.test.functional.internal.classpath

import org.gradle.api.Transformer
import org.gradle.internal.ErroringAction
import org.gradle.internal.IoActions
import org.gradle.internal.UncheckedException
import org.gradle.internal.classloader.ClasspathUtil
import org.gradle.util.CollectionUtils
import org.gradle.util.TextUtil

public class ClasspathAddingInitScriptBuilder {

    public void build(File initScriptFile, final ClassLoader classLoader) {
        build(initScriptFile, getClasspathAsFiles(classLoader));
    }

    public void build(File initScriptFile, final List<File> classpath) {
        def commaSeparatedClasspathFiles = commaSeparateFiles(classpath)

        IoActions.writeTextFile(initScriptFile, new ErroringAction<Writer>() {
            @Override
            protected void doExecute(Writer writer) throws Exception {
                writer.write("""
allprojects {
    buildscript {
        dependencies {
            classpath files($commaSeparatedClasspathFiles)
        }
    }
}
""")
            }
        });
    }

    /**
     * Turn a list of File instances into a comma-separated list of file paths represented as single String.
     *
     * @param files Files
     * @return Comma-separated files
     */
    private String commaSeparateFiles(List<File> files) {
        def classpathFiles = files.collect { file -> "'${TextUtil.escapeString(file.getAbsolutePath())}'" }
        classpathFiles.join(', ')
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
