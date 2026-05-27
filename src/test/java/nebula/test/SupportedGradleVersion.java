package nebula.test;

import nebula.test.dsl.Gradle;
import org.jspecify.annotations.NullMarked;

@NullMarked
public enum SupportedGradleVersion {
    MIN(Gradle.ofVersion("9.0.0")), CURRENT(Gradle.current());

    public final Gradle version;

    SupportedGradleVersion(Gradle version) {
        this.version = version;
    }
}
