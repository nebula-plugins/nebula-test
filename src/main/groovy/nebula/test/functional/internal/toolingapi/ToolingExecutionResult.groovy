package nebula.test.functional.internal.toolingapi

import nebula.test.functional.internal.DefaultExecutionResult

/**
 * Hold additional response data, that is only available
 */
class ToolingExecutionResult extends DefaultExecutionResult {

    ToolingExecutionResult(Boolean success, String standardOutput, String standardError,  List<MinimalExecutedTask> executedTasks, Throwable failure) {
        super(success, standardOutput, standardError, executedTasks, failure)
    }

}
