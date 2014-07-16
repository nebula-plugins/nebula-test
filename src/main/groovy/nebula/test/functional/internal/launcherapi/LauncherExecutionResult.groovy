package nebula.test.functional.internal.launcherapi

import nebula.test.functional.internal.DefaultExecutionResult
import org.gradle.BuildResult
import org.gradle.api.invocation.Gradle

/**
 * Hold additional response data, that is only available
 */
class LauncherExecutionResult extends DefaultExecutionResult {

    BuildResult buildResult

    LauncherExecutionResult(Boolean success, String standardOutput, String standardError, Throwable failure, List<StateExecutedTask> executedTasks, BuildResult buildResult) {
        super(success, standardOutput, standardError, executedTasks, failure)
        this.buildResult = buildResult
    }

    StateExecutedTask task(String name) {
        executedTasks.find { ((StateExecutedTask) it).task.path == name }
    }

    Collection<StateExecutedTask> tasks(String... names) {
        def tasks = executedTasks.findAll { ((StateExecutedTask) it).task.path in names }
        assert tasks.size() == names.size()
        tasks
    }

    Gradle getGradle() {
        return buildResult.gradle
    }
}
