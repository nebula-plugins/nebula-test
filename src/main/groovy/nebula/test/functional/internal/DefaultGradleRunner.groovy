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

import nebula.test.functional.ExecutionResult;
import nebula.test.functional.GradleRunner;

public class DefaultGradleRunner implements GradleRunner {

    private final GradleHandleFactory handleFactory;

    public DefaultGradleRunner(GradleHandleFactory handleFactory) {
        this.handleFactory = handleFactory;
    }

    public ExecutionResult run(File projectDir, List<String> arguments) {
        return handle(projectDir, arguments).run();
    }

    public GradleHandle handle(File projectDir, List<String> arguments) {
        return handleFactory.start(projectDir, arguments);
    }

}
