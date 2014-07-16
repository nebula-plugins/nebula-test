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

package nebula.test.functional.internal.launcherapi

import nebula.test.functional.ExecutionResult
import nebula.test.functional.internal.GradleHandle
import org.gradle.BuildResult
import org.gradle.GradleLauncher
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import org.gradle.logging.internal.StreamBackedStandardOutputListener

public class GradleLauncherBackedGradleHandle implements GradleHandle {

    final private ByteArrayOutputStream standardOutput = new ByteArrayOutputStream();
    final private ByteArrayOutputStream standardError = new ByteArrayOutputStream();

    final private GradleLauncher launcher
    final private List<StateExecutedTask> executedTasks = []

    public GradleLauncherBackedGradleHandle(GradleLauncher launcher) {
        launcher.addStandardErrorListener(new StreamBackedStandardOutputListener(standardError));
        launcher.addStandardOutputListener(new StreamBackedStandardOutputListener(standardOutput));

        // Executed Tasks
        launcher.addListener(new TaskExecutionListener() {
            void beforeExecute(Task task) {
                executedTasks << new StateExecutedTask(task: task)
            }

            void afterExecute(Task task, TaskState taskState) {
                executedTasks.last().state = taskState
            }
        })
        this.launcher = launcher
    }

    private String getStandardOutput() {
        return standardOutput.toString();
    }

    private String getStandardError() {
        return standardError.toString();
    }

    public ExecutionResult run() {
        Throwable failure
        BuildResult buildResult

        // Reset state from previous run
        executedTasks.clear()
        standardOutput.reset()
        standardError.reset()

        try {
            buildResult = launcher.run()
        } catch(Exception e) {
            failure = e
        }

        Throwable determinedFailure = failure ?: buildResult.failure
        boolean success = determinedFailure == null
        return new LauncherExecutionResult(success, getStandardOutput(), getStandardError(), determinedFailure, executedTasks, buildResult);
    }

}
