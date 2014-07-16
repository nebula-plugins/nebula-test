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

package nebula.test.functional.internal.toolingapi;

import nebula.test.functional.ExecutionResult;
import nebula.test.functional.internal.DefaultExecutionResult;
import nebula.test.functional.internal.GradleHandle;
import org.gradle.tooling.BuildException;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.ProgressEvent;
import org.gradle.tooling.ProgressListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BuildLauncherBackedGradleHandle implements GradleHandle {

    final private ByteArrayOutputStream standardOutput = new ByteArrayOutputStream();
    final private ByteArrayOutputStream standardError = new ByteArrayOutputStream();
    final private BuildLauncher launcher;
    final private List<String> tasksExecuted;
    public static final String PROGRESS_TASK_PREFIX = "Execute :";

    public BuildLauncherBackedGradleHandle(BuildLauncher launcher) {
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

    private String getStandardOutput() {
        return standardOutput.toString();
    }

    private String getStandardError() {
        return standardError.toString();
    }

    public ExecutionResult run() {
        Throwable failure = null;
        try {
            launcher.run();
        } catch(BuildException e) {
            failure = e.getCause();
        } catch(Exception e) {
            failure = e;
        }

        String stdout = getStandardOutput();
        List<MinimalExecutedTask> tasks = new ArrayList<MinimalExecutedTask>();
        for (String taskName: tasksExecuted) {
            // Scan stdout for task's up to date
            boolean upToDate = stdout.contains(taskName + " UP-TO-DATE");
            tasks.add( new MinimalExecutedTask(taskName, upToDate) );
        }
        boolean success = failure == null
        return new ToolingExecutionResult(success, stdout, getStandardError(), tasks, failure);
    }

}
