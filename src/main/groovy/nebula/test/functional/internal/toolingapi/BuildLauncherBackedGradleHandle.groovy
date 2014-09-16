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

package nebula.test.functional.internal.toolingapi

import nebula.test.functional.ExecutionResult
import nebula.test.functional.internal.GradleHandle
import nebula.test.functional.internal.GradleHandleBuildListener
import org.gradle.tooling.BuildException
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.ProgressEvent
import org.gradle.tooling.ProgressListener

public class BuildLauncherBackedGradleHandle implements GradleHandle {

    final private ByteArrayOutputStream standardOutput = new ByteArrayOutputStream();
    final private ByteArrayOutputStream standardError = new ByteArrayOutputStream();
    final private BuildLauncher launcher;
    final private boolean forkedProcess
    final private List<String> tasksExecuted;
    public static final String PROGRESS_TASK_PREFIX = "Execute :";
    private GradleHandleBuildListener buildListener

    public BuildLauncherBackedGradleHandle(BuildLauncher launcher, boolean forkedProcess) {
        this.forkedProcess = forkedProcess
        launcher.setStandardOutput(standardOutput);
        launcher.setStandardError(standardError);

        tasksExecuted = new ArrayList<String>();
        launcher.addProgressListener(new ProgressListener() {
            @Override
            public void statusChanged(ProgressEvent event) {
                // These are free form strings, :-(
                if (event.getDescription().startsWith(PROGRESS_TASK_PREFIX)) { // E.g. "Execute :echo"
                    String taskName = event.getDescription().substring(PROGRESS_TASK_PREFIX.length() - 1);
                    tasksExecuted.add(taskName);
                }
            }
        });
        this.launcher = launcher;
    }

    @Override
    void registerBuildListener(GradleHandleBuildListener buildListener) {
        this.buildListener = buildListener
    }

    @Override
    boolean isForkedProcess() {
        forkedProcess
    }

    private String getStandardOutput() {
        return standardOutput.toString();
    }

    private String getStandardError() {
        return standardError.toString();
    }

    public ExecutionResult run() {
        Throwable failure = null;
        try {
            buildListener?.buildStarted()
            launcher.run();
        } catch(BuildException e) {
            failure = e.getCause();
        } catch(Exception e) {
            failure = e;
        }
        finally {
            buildListener?.buildFinished()
        }

        String stdout = getStandardOutput();
        List<MinimalExecutedTask> tasks = new ArrayList<MinimalExecutedTask>();
        for (String taskName: tasksExecuted) {
            // Scan stdout for task's up to date
            boolean upToDate = isTaskUpToDate(stdout, taskName)
            boolean skipped = isTaskSkipped(stdout, taskName)
            tasks.add( new MinimalExecutedTask(taskName, upToDate, skipped) );
        }
        boolean success = failure == null
        return new ToolingExecutionResult(success, stdout, getStandardError(), tasks, failure);
    }

    private isTaskUpToDate(String stdout, String taskName) {
        containsOutput(stdout, taskName, 'UP-TO-DATE')
    }

    private isTaskSkipped(String stdout, String taskName) {
        containsOutput(stdout, taskName, 'SKIPPED')
    }

    private boolean containsOutput(String stdout, String taskName, String stateIdentifier) {
        stdout.contains("$taskName $stateIdentifier".toString())
    }
}
