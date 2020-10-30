/*
 * Copyright 2015-2017 Netflix, Inc.
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
package nebula.test.functional

import com.google.common.base.StandardSystemProperty
import com.google.common.collect.FluentIterable
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Issue
import spock.lang.Specification

/**
 * Tests for predicates that live on {@link GradleRunner}.
 */
class GradleRunnerSpec extends Specification {
    List<URL> classpath

    def workDir = new File(StandardSystemProperty.USER_DIR.value())
    def siblingDir = new File(workDir.parentFile, 'sibling')
    def sharedDependencyCache = new File(workDir.parentFile, 'sharedDependencyCache')

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables()

    def setup() {
        // Partial real-world classpath from a IntegrationSpec launch, only the workDir/siblingDir paths matter, otherwise these are just string comparisons
        def classpathUris = [
                "file:/Applications/IntelliJ%20IDEA%2015%20EAP.app/Contents/lib/serviceMessages.jar",
                "file:/Applications/IntelliJ%20IDEA%2015%20EAP.app/Contents/lib/idea_rt.jar",
                "file:/Applications/IntelliJ%20IDEA%2015%20EAP.app/Contents/plugins/junit/lib/junit-rt.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/lib/ant-javafx.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/lib/dt.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/lib/javafx-doclet.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/lib/javafx-mx.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/lib/jconsole.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/lib/sa-jdi.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/lib/tools.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/charsets.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/deploy.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/htmlconverter.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/javaws.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/jce.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/jfr.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/jfxrt.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/jsse.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/management-agent.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/plugin.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/resources.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/rt.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/ext/dnsns.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/ext/localedata.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/ext/sunec.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar",
                "file:/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/ext/zipfs.jar",

                // The project that is being tested always appears as follows:
                new File(workDir, 'build/classes/test/').toURI() as String,
                new File(workDir, 'build/classes/main/').toURI() as String,
                new File(workDir, 'build/resources/test/').toURI() as String,
                new File(workDir, 'build/resources/main/').toURI() as String,

                // when launched from IDE, project dependencies appear this way:
                new File(siblingDir, 'build/classes/test/').toURI() as String,
                new File(siblingDir, 'build/classes/main/').toURI() as String,
                new File(siblingDir, 'build/resources/test/').toURI() as String,
                new File(siblingDir, 'build/resources/main/').toURI() as String,

                // when launched from IntelliJ, and not delegating to Gradle for compilation, project dependencies appear this way:
                new File(siblingDir, 'out/integTest/classes').toURI() as String,
                new File(siblingDir, 'out/test/classes').toURI() as String,
                new File(siblingDir, 'out/test/resources').toURI() as String,
                new File(siblingDir, 'out/production/classes').toURI() as String,
                new File(siblingDir, 'out/production/resources').toURI() as String,

                // when launched from Gradle, project dependencies appear as jars:
                new File(siblingDir, 'build/libs/repos-4.0.0.jar').toURI() as String,

                new File(workDir, ".gradle/caches/modules-2/files-2.1/org.spockframework/spock-core/1.0-groovy-2.3/762fbf6c5f24baabf9addcf9cf3647151791f7eb/spock-core-1.0-groovy-2.3.jar").toURI() as String,
                new File(workDir, ".gradle/caches/modules-2/files-2.1/cglib/cglib-nodep/2.2.2/d456bb230c70c0b95c76fb28e429d42f275941/cglib-nodep-2.2.2.jar").toURI() as String,
                new File(workDir, ".gradle/caches/modules-2/files-2.1/commons-lang/commons-lang/2.6/ce1edb914c94ebc388f086c6827e8bdeec71ac2/commons-lang-2.6.jar").toURI() as String,
                new File(workDir, ".gradle/caches/modules-2/files-2.1/junit/junit/4.12/2973d150c0dc1fefe998f834810d68f278ea58ec/junit-4.12.jar").toURI() as String,
                new File(workDir, ".gradle/caches/modules-2/files-2.1/org.hamcrest/hamcrest-core/1.3/42a25dc3219429f0e5d060061f71acb49bf010a0/hamcrest-core-1.3.jar").toURI() as String,
                new File(workDir, ".gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/gradle-core-2.2.1.jar").toURI() as String,
                new File(workDir, ".gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/groovy-all-2.3.6.jar").toURI() as String,
                new File(workDir, ".gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/asm-all-5.0.3.jar").toURI() as String,
                new File(workDir, ".gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/ant-1.9.3.jar").toURI() as String,
                new File(workDir, ".gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/commons-collections-3.2.1.jar").toURI() as String,
                new File(workDir, ".gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/commons-io-1.4.jar").toURI() as String,

                new File(sharedDependencyCache, "/modules-2/files-2.1/junit/junit/4.13/2973d150c0dc1fefe998f834810d68f278ea58ec/junit-4.13.jar").toURI() as String
        ]
        classpath = classpathUris.collect { new URI(it).toURL() }
    }

    def 'gradle distribution predicate matches expected files'() {
        expect:
        def filtered = FluentIterable.from(classpath).filter(GradleRunner.CLASSPATH_GRADLE_CACHE).toList()
        filtered.size() == 5
    }


    def 'gradle distribution predicate matches expected files with GRADLE_RO_DEP_CACHE support'() {
        setup:
        environmentVariables.set("GRADLE_RO_DEP_CACHE", sharedDependencyCache.absolutePath)

        expect:
        def filtered = FluentIterable.from(classpath).filter(GradleRunner.CLASSPATH_GRADLE_CACHE).toList()
        filtered.size() == 6
        filtered.any { it.file.contains('junit-4.13') }
    }

    def 'jvm predicate matches expected files'() {
        expect:
        def filtered = FluentIterable.from(classpath).filter(GradleRunner.CLASSPATH_PROJECT_DIR).toList()
        filtered.size() == 15
    }

    def 'project dependencies matches expected files'() {
        expect:
        def filtered = FluentIterable.from(classpath).filter(GradleRunner.CLASSPATH_PROJECT_DEPENDENCIES).toList()
        filtered.size() == 14
    }

    def 'default classpath matches only application class paths and dependencies'() {
        expect:
        def filtered = FluentIterable.from(classpath).filter(GradleRunner.CLASSPATH_DEFAULT).toList()
        filtered.size() == 25
    }
}
