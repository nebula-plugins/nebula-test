package nebula.test.dsl;

import org.assertj.core.api.AbstractAssert;
import org.gradle.testkit.runner.BuildResult;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;

/**
 * Assertions for the overall build result
 */
@NullMarked
public class BuildResultAssert extends AbstractAssert<BuildResultAssert, BuildResult> {
    BuildResultAssert(BuildResult buildResult) {
        super(buildResult, BuildResultAssert.class);
    }

    @Contract(" -> this")
    public BuildResultAssert hasNoDeprecationWarnings() {
        if (actual.getOutput().contains("has been deprecated and is scheduled to be removed in Gradle") ||
            actual.getOutput().contains("Deprecated Gradle features were used in this build") ||
            actual.getOutput().contains("has been deprecated. This is scheduled to be removed in Gradle") ||
            actual.getOutput().contains("This will fail with an error in Gradle") ||
            actual.getOutput().contains("This behaviour has been deprecated and is scheduled to be removed in Gradle")) {
            failWithMessage("Build output has deprecation warnings: <%s>", actual.getOutput());
        }
        return this;
    }

    @Contract(" -> this")
    public BuildResultAssert hasNoMutableStateWarnings() {
        if (actual.getOutput().contains("was resolved without accessing the project in a safe manner") ||
            actual.getOutput().contains("This may happen when a configuration is resolved from a thread not managed by Gradle or from a different project") ||
            actual.getOutput().contains("was resolved from a thread not managed by Gradle.") ||
            actual.getOutput().contains("was attempted from a context different than the project context")) {
            failWithMessage("Build output has mutable state warnings: <%s>", actual.getOutput());
        }
        return this;
    }

    public BuildTaskAssert task(String path) {
        return new BuildTaskAssert(actual.task(path));
    }
}
