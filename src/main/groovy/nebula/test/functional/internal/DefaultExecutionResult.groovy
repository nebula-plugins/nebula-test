/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nebula.test.functional.internal

import groovy.transform.CompileStatic
import nebula.test.functional.ExecutionResult
import org.gradle.api.GradleException

@CompileStatic
abstract class DefaultExecutionResult implements ExecutionResult {
    private final Boolean success
    private final String standardOutput
    private final String standardError
    private final List<? extends ExecutedTask> executedTasks
    private final Throwable failure

    public DefaultExecutionResult(Boolean success, String standardOutput, String standardError, List<? extends ExecutedTask> executedTasks, Throwable failure) {
        this.success = success
        this.standardOutput = standardOutput
        this.standardError = standardError
        this.executedTasks = executedTasks
        this.failure = failure
    }

    @Override
    Boolean getSuccess() {
        success
    }

    @Override
    public String getStandardOutput() {
        standardOutput
    }

    @Override
    public String getStandardError() {
        standardError
    }

    @Override
    boolean wasExecuted(String taskPath) {
        executedTasks.any { ExecutedTask task ->
            taskPath = normalizeTaskPath(taskPath)
            def match = task.path == taskPath
            return match
        }
    }

    @Override
    boolean wasUpToDate(String taskPath) {
        getExecutedTaskByPath(taskPath).upToDate
    }

    @Override
    boolean wasSkipped(String taskPath) {
        getExecutedTaskByPath(taskPath).skipped
    }

    @Override
    boolean noSource(String taskPath) {
        getExecutedTaskByPath(taskPath).noSource
    }

    String normalizeTaskPath(String taskPath) {
        taskPath.startsWith(':') ? taskPath : ":$taskPath"
    }

    private ExecutedTask getExecutedTaskByPath(String taskPath) {
        taskPath = normalizeTaskPath(taskPath)
        def task = executedTasks.find { ExecutedTask task -> task.path == taskPath }
        if (task == null) {
            throw new RuntimeException("Task with path $taskPath was not found")
        }
        task
    }

    @Override
    public Throwable getFailure() {
        failure
    }

    @Override
    public ExecutionResult rethrowFailure() {
        if (failure instanceof GradleException) {
            throw (GradleException) failure
        }
        if (failure != null) {
            throw new GradleException("Build aborted because of an internal error.", failure)
        }
        this
    }

}
