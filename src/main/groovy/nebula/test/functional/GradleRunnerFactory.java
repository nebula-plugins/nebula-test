package nebula.test.functional;

import nebula.test.functional.internal.DefaultGradleRunner;
import nebula.test.functional.internal.GradleHandleFactory;
import nebula.test.functional.internal.classpath.ClasspathInjectingGradleHandleFactory;
import nebula.test.functional.internal.toolingapi.ToolingApiGradleHandleFactory;

public class GradleRunnerFactory {

    public static GradleRunner create() {
        GradleHandleFactory toolingApiHandleFactory = new ToolingApiGradleHandleFactory();

        // TODO: Which class would be attached to the right classloader? Is using something from the test kit right?
        ClassLoader sourceClassLoader = GradleRunnerFactory.class.getClassLoader();
        GradleHandleFactory classpathInjectingHandleFactory = new ClasspathInjectingGradleHandleFactory(sourceClassLoader, toolingApiHandleFactory);

        return new DefaultGradleRunner(classpathInjectingHandleFactory);
    }

}
