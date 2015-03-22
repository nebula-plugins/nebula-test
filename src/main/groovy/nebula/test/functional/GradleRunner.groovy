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

package nebula.test.functional;

import nebula.test.functional.internal.GradleHandle;

public interface GradleRunner {

    /**
     * Create handle and run build
     * @param directory
     * @param args
     * @return results from execution
     */
    ExecutionResult run(File directory, List<String> args)

    ExecutionResult run(File directory, List<String> args, List<String> jvmArgs)

    /**
     * Handle on instance of Gradle that can be run.
     * @param directory
     * @param args
     * @return handle
     */
    GradleHandle handle(File directory, List<String> args)

    GradleHandle handle(File directory, List<String> args, List<String> jvmArgs)
}
