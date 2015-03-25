package nebula.test.functional.internal;

public interface GradleHandleFactory {

    GradleHandle start(File dir, List<String> arguments);

    GradleHandle start(File dir, List<String> arguments, List<String> jvmArguments);
}
