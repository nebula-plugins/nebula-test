/*
 * Copyright 2012-2017 the original author or authors.
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

import nebula.test.functional.internal.GradleHandle

import java.util.function.Predicate

interface GradleRunner {
    // These predicates are here, instead of on GradleRunnerFactory due to a Groovy static compiler bug (https://issues.apache.org/jira/browse/GROOVY-7159)

    static final String SHARED_DEPENDENCY_CACHE_ENVIRONMENT_VARIABLE = 'GRADLE_RO_DEP_CACHE'

    static final Predicate<URL> CLASSPATH_GRADLE_CACHE = new Predicate<URL>() {
        @Override
        boolean test(URL url) {
            String gradleSharedDependencyCache = System.getenv(SHARED_DEPENDENCY_CACHE_ENVIRONMENT_VARIABLE)
            boolean cachedModule = url.path.contains('/caches/modules-')
            boolean readOnlyCachedModule = gradleSharedDependencyCache && url.path.contains("${gradleSharedDependencyCache}/modules-")
            boolean testDistributionOrphanedFile = url.path.contains('/orphan-files/') // test distribution orphans read-only dependency cache files
            return (cachedModule || readOnlyCachedModule || testDistributionOrphanedFile) && !isTestingFramework(url)
        }

        static boolean isTestingFramework(URL url) {
            return url.path.contains("spock-") || url.path.contains("junit-")
        }
    }

    static final Predicate<URL> MAVEN_LOCAL = new Predicate<URL>() {
        @Override
        boolean test(URL url) {
            String m2RepositoryPrefix = System.getProperty("user.home") + "/.m2/repository"
            return url.path.contains(m2RepositoryPrefix)
        }
    }

    static final Predicate<URL> CLASSPATH_PROJECT_DIR = new Predicate<URL>() {
        @Override
        boolean test(URL url) {
            File userDir = new File(System.getProperty("user.dir"))
            return url.path.startsWith(userDir.toURI().toURL().path)
        }
    }

    static final Predicate<URL> CLASSPATH_PROJECT_DEPENDENCIES = new Predicate<URL>() {
        @Override
        boolean test(URL url) {
            return url.path.contains('/build/classes') || url.path.contains('/build/resources') || url.path.contains('/build/libs') || url.path.contains('/out/')
        }
    }

    /**
     * Attempts to provide a classpath that approximates the 'normal' Gradle runtime classpath. Use {@link #CLASSPATH_ALL}
     * to default to pre-2.2.2 behaviour.
     */
    static final Predicate<URL> CLASSPATH_DEFAULT = new Predicate<URL>() {
        @Override
        boolean test(URL url) {
            return CLASSPATH_PROJECT_DIR.test(url) || CLASSPATH_GRADLE_CACHE.test(url) || CLASSPATH_PROJECT_DEPENDENCIES.test(url) || MAVEN_LOCAL.test(url)
        }
    }

    /**
     * Accept all URLs. Provides pre-2.2.2 behaviour.
     */
    static final Predicate<URL> CLASSPATH_ALL = new Predicate<URL>() {
        @Override
        boolean test(URL url) {
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

    ExecutionResult run(File directory, List<String> args, List<String> jvmArgs, List<PreExecutionAction> preExecutionActions)

    /**
     * Handle on instance of Gradle that can be run.
     * @param directory
     * @param args
     * @return handle
     */
    GradleHandle handle(File directory, List<String> args)

    GradleHandle handle(File directory, List<String> args, List<String> jvmArgs)

    GradleHandle handle(File directory, List<String> args, List<String> jvmArgs, List<PreExecutionAction> preExecutionActions)
}
