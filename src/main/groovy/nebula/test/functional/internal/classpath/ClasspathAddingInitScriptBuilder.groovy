package nebula.test.functional.internal.classpath;

import org.gradle.api.Transformer;
import org.gradle.internal.ErroringAction;
import org.gradle.internal.IoActions;
import org.gradle.internal.UncheckedException;
import org.gradle.internal.classloader.ClasspathUtil;
import org.gradle.util.CollectionUtils;
import org.gradle.util.TextUtil;

import java.io.File;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClasspathAddingInitScriptBuilder {

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
                    writer.write(String.format("      classpath file('%s')\n", TextUtil.escapeString(file.getAbsolutePath())));
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
