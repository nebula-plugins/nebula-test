package nebula.test.functional

import groovy.transform.CompileStatic
import nebula.test.functional.internal.DefaultGradleRunner
import nebula.test.functional.internal.GradleHandleFactory
import nebula.test.functional.internal.classpath.ClasspathInjectingGradleHandleFactory
import nebula.test.functional.internal.toolingapi.ToolingApiGradleHandleFactory

import java.util.function.Predicate

/**
 * @deprecated in favor of TestKit-based tooling
 */
@Deprecated
@CompileStatic
class GradleRunnerFactory {
    public static GradleRunner createTooling(boolean fork = false, String version = null, Integer daemonMaxIdleTimeInSeconds = null,
                                             Predicate<URL> classpathFilter = null) {
        GradleHandleFactory toolingApiHandleFactory = new ToolingApiGradleHandleFactory(fork, version, daemonMaxIdleTimeInSeconds);
        return create(toolingApiHandleFactory, classpathFilter ?: GradleRunner.CLASSPATH_DEFAULT);
    }

    public static GradleRunner create(GradleHandleFactory handleFactory, Predicate<URL> classpathFilter = null) {
        // TODO: Which class would be attached to the right classloader? Is using something from the test kit right?
        ClassLoader sourceClassLoader = GradleRunnerFactory.class.getClassLoader();
        create(handleFactory, sourceClassLoader, classpathFilter ?: GradleRunner.CLASSPATH_DEFAULT)
    }

    public static GradleRunner create(GradleHandleFactory handleFactory, ClassLoader sourceClassLoader, Predicate<URL> classpathFilter) {
        GradleHandleFactory classpathInjectingHandleFactory = new ClasspathInjectingGradleHandleFactory(sourceClassLoader, handleFactory, classpathFilter);
        return new DefaultGradleRunner(classpathInjectingHandleFactory);
    }
}
