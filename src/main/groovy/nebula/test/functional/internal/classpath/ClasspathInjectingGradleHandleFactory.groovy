package nebula.test.functional.internal.classpath

import nebula.test.functional.internal.GradleHandle
import nebula.test.functional.internal.GradleHandleFactory
import org.gradle.util.GFileUtils

public class ClasspathInjectingGradleHandleFactory implements GradleHandleFactory {

    private final ClassLoader classLoader;
    private final GradleHandleFactory delegateFactory;

    public ClasspathInjectingGradleHandleFactory(ClassLoader classLoader, GradleHandleFactory delegateFactory) {
        this.classLoader = classLoader;
        this.delegateFactory = delegateFactory;
    }

    @Override
    public GradleHandle start(File projectDir, List<String> arguments, List<String> jvmArguments = []) {
        File testKitDir = new File(projectDir, ".gradle-test-kit");
        if (!testKitDir.exists()) {
            GFileUtils.mkdirs(testKitDir);
        }

        File initScript = new File(testKitDir, "init.gradle");
        new ClasspathAddingInitScriptBuilder().build(initScript, classLoader);

        List<String> ammendedArguments = new ArrayList<String>(arguments.size() + 2);
        ammendedArguments.add("--init-script");
        ammendedArguments.add(initScript.getAbsolutePath());
        ammendedArguments.addAll(arguments);
        return delegateFactory.start(projectDir, ammendedArguments, jvmArguments);
    }
}
