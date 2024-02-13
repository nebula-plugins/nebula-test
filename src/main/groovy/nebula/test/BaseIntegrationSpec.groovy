/*
 * Copyright 2013-2018 Netflix, Inc.
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
import org.junit.Rule
import org.junit.rules.TestName
import spock.lang.Specification

/**
 * {@link Specification} implementation of the {@link IntegrationBase}.
 */
@CompileStatic
/**
 * BaseIntegrationSpec is not recommended as it is not compatible with Gradle's instrumentation mechanisms
 * ex. https://github.com/gradle/gradle/issues/27956 and https://github.com/gradle/gradle/issues/27639
 *
 * This will be removed in the next nebula-test major version
 */
abstract class BaseIntegrationSpec extends Specification implements IntegrationBase {
    @Rule
    TestName testName = new TestName()

    void setup() {
        IntegrationBase.super.initialize(getClass(), testName.methodName)
    }
}
