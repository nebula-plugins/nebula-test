/*
 * Copyright 2013-2018 the original author or authors.
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
package nebula.test


import groovy.transform.CompileStatic

/**
 * @author Justin Ryan
 * @author Marcin Erdmann
 */
@CompileStatic
/**
 * IntegrationSpec is not recommended as it is not compatible with Gradle's instrumentation mechanisms
 * ex. https://github.com/gradle/gradle/issues/27956 and https://github.com/gradle/gradle/issues/27639
*
 * This will be removed in the next nebula-test major version
 */
@Deprecated
abstract class IntegrationSpec extends BaseIntegrationSpec implements Integration {
    def setup() {
        Integration.super.initialize(getClass(), testName.methodName)
    }
}
