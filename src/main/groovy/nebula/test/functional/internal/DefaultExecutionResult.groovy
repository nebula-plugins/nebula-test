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

package nebula.test.functional.internal;

import nebula.test.functional.ExecutionResult
import org.gradle.api.GradleException;

public abstract class DefaultExecutionResult implements ExecutionResult {

    private final String standardOutput;
    private final String standardError;
    private final List<? extends ExecutedTask> executedTasks
    private final Throwable failure

    public DefaultExecutionResult(String standardOutput, String standardError, List<? extends ExecutedTask> executedTasks, Throwable failure) {
        this.standardOutput = standardOutput
        this.standardError = standardError
        this.executedTasks = executedTasks
        this.failure = failure
    }

    public String getStandardOutput() {
        return standardOutput;
    }

    public String getStandardError() {
        return standardError;
    }

    boolean wasExecuted(String taskPath) {
        executedTasks.any {
            def match = it.path == taskPath
            return match
        }
    }

    boolean wasUpToDate(String taskPath) {
        def task = executedTasks.find { it.path == taskPath }
        if (task == null) {
            throw RuntimeException("Task with path $taskPath was not found")
        }
        return task.upToDate
    }

    public Throwable getFailure() {
        return failure
    }

    public ExecutionResult rethrowFailure() {
        if (failure instanceof GradleException) {
            throw (GradleException) failure;
        }
        if (failure != null) {
            throw new GradleException("Build aborted because of an internal error.", failure);
        }
        return this;
    }

}
