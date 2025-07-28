package nebula.test.dsl;

import org.assertj.core.api.Assertions;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Provides assertions to be used with TEstKit results
 */
@NullMarked
public class TestKitAssertions extends Assertions {

    /**
     * Assertions for the overall build result
     * @param actual the actual result to assert on
     * @return a new instance of the build result assertion object
     */
    @Contract("_ -> new")
    public static BuildResultAssert assertThat(BuildResult actual) {
        return new BuildResultAssert(actual);
    }

    /**
     * Assertions for a specific task
     * @param actual the actual task result to assert on
     * @return a new instance of the task result assertion object
     */
    @Contract("_ -> new")
    public static BuildTaskAssert assertThat(@Nullable BuildTask actual) {
        return new BuildTaskAssert(actual);
    }
}
