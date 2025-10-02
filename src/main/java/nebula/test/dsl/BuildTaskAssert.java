package nebula.test.dsl;

import org.assertj.core.api.AbstractAssert;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.TaskOutcome;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Assertions for individual tasks
 */
@NullMarked
public class BuildTaskAssert extends AbstractAssert<BuildTaskAssert, @Nullable BuildTask> {
    BuildTaskAssert(@Nullable BuildTask buildTask) {
        super(buildTask, BuildTaskAssert.class);
    }

    /**
     * fluent entry point
     *
     * @param actual the actual task to assert on
     * @return a new instance of the task assertion
     */
    @Contract("_ -> new")
    public static BuildTaskAssert assertThat(BuildTask actual) {
        return new BuildTaskAssert(actual);
    }

    /**
     * Assert that the task was run and resulted in the expected outcome
     */
    @Contract("_ -> this")
    public BuildTaskAssert hasOutcome(TaskOutcome outcome) {
        this.objects.assertNotNull(this.info, actual);
        if (actual.getOutcome() != outcome) {
            failWithMessage("Expected outcome of task <%s> is <%s> but was <%s>",
                    actual.getPath(), outcome, actual.getOutcome());
        }
        return this;
    }

    /**
     * Assert that the task was run and resulted in any of the expected outcomes
     */
    @Contract("_ -> this")
    public BuildTaskAssert hasOutcome(TaskOutcome... outcomes) {
        this.objects.assertNotNull(this.info, actual);
        final List<TaskOutcome> expected = Arrays.asList(outcomes);
        if (!expected.contains(actual.getOutcome())) {
            failWithMessage("Expected outcome of task <%s> to be one of <%s> but was <%s>",
                    actual.getPath(),
                    expected.stream().map(Enum::name).collect(Collectors.joining(",")),
                    actual.getOutcome());
        }
        return this;
    }
}
