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

package nebula.test.functional

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import com.google.common.base.StandardSystemProperty
import nebula.test.functional.internal.GradleHandle

public interface GradleRunner {
    // These predicates are here, instead of on GradleRunnerFactory due to a Groovy static compiler bug

    static final Predicate<URL> CLASSPATH_GRADLE_CACHE = new Predicate<URL>() {
        @Override
        boolean apply(URL url) {
            return url.path.contains('/caches/modules-')
        }
    }

    static final Predicate<URL> CLASSPATH_USER_DIR = new Predicate<URL>() {
        @Override
        boolean apply(URL url) {
            File userDir = new File(StandardSystemProperty.USER_DIR.value())
            return url.path.startsWith(userDir.path)
        }
    }

    /**
     * Attempts to provide a classpath that approximates the 'normal' Gradle runtime classpath. Use {@link #CLASSPATH_ALL}
     * to default to pre-2.2.2 behaviour.
     */
    static final Predicate<URL> CLASSPATH_DEFAULT = Predicates.or(CLASSPATH_USER_DIR, CLASSPATH_GRADLE_CACHE)

    /**
     * Accept all URLs. Provides pre-2.2.2 behaviour.
     */
    static final Predicate<URL> CLASSPATH_ALL = new Predicate<URL>() {
        @Override
        boolean apply(URL url) {
            return true
        }
    }

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
