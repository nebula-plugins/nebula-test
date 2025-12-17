import nebula.plugin.contacts.Contact
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/*
 * Copyright 2014-2019 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    id("com.netflix.nebula.plugin-plugin")
    id("com.netflix.nebula.archrules.library")
    id("java-library")
    `kotlin-dsl`
    jacoco
}

description = "Test harness for Gradle plugins. Hopefully retiring in favor of Gradle TestKit"

contacts {
    (addPerson("nebula-plugins-oss@netflix.com") as Contact).apply {
        moniker = "Nebula Plugins Maintainers"
        github = "nebula-plugins"
    }
}

dependencies {
    compileOnly(localGroovy())
    api("org.jspecify:jspecify:1.0.0")
    api("org.assertj:assertj-core:3.27.3")
    compileOnly(gradleTestKit())
    compileOnly("org.spockframework:spock-core:2.3-groovy-4.0")
    compileOnly("org.spockframework:spock-junit4:2.3-groovy-4.0")
    api("org.junit.platform:junit-platform-launcher:1.+")
    runtimeOnly("cglib:cglib-nodep:3.2.2")
    runtimeOnly("org.objenesis:objenesis:2.4")

    testImplementation("org.spockframework:spock-core:2.3-groovy-4.0")
    testImplementation("org.spockframework:spock-junit4:2.3-groovy-4.0")
    testImplementation("uk.org.webcompere:system-stubs-junit4:2.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.13.4")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.13.4")
    archRulesTestImplementation("org.spockframework:spock-junit4:2.3-groovy-4.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = 2
    finalizedBy(tasks.named("jacocoTestReport"))
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
tasks.named<JavaCompile>("compileTestJava") {
    javaCompiler.set(
        javaToolchains.compilerFor {
            languageVersion = JavaLanguageVersion.of(17)
        }
    )
}
tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinJavaToolchain.toolchain.use(
        javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(17)
        }
    )
}

tasks.named("build") {
    dependsOn(gradle.includedBuild("gradleTest").task(":build"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}

tasks.named("publishPlugins") { enabled = false }

tasks.wrapper {
    distributionType = Wrapper.DistributionType.BIN
    gradleVersion = "9.0.0"
    distributionSha256Sum = "8fad3d78296ca518113f3d29016617c7f9367dc005f932bd9d93bf45ba46072b"
}