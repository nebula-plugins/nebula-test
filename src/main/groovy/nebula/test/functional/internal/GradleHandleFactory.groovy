package nebula.test.functional.internal;

public interface GradleHandleFactory {

    GradleHandle start(File dir, List<String> arguments);

}
