package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;

/**
 * A representation of a gradle distribution to use with testkit
 * This may be a version of a standard distribution, the url of custom distribution, or reuse the current gradle distribution
 */
@NullMarked
public sealed interface Gradle permits Gradle.CurrentGradle, Gradle.GradleDistribution, Gradle.GradleVersion {
    static Gradle ofDistribution(String url) {
        return new GradleDistribution(url);
    }

    static Gradle ofVersion(String version) {
        return new GradleVersion(version);
    }

    static Gradle current() {
        return new CurrentGradle();
    }

    /**
     * Represents the current gradle distribution being used to execute the testkit test suite
     */
    final class CurrentGradle implements Gradle {
    }

    /**
     *
     * @param url a full url to a custom gradle distribution
     */
    record GradleDistribution(String url) implements Gradle {

    }

    /**
     *
     * @param version a version number, such as "9.4.1"
     */
    record GradleVersion(String version) implements Gradle {
    }
}


