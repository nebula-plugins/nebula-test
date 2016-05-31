package nebula.test.functional

import com.google.common.base.StandardSystemProperty
import com.google.common.collect.FluentIterable
import nebula.test.functional.internal.classpath.ClasspathUserDirPredicate
import spock.lang.Specification
/**
 * Tests for predicates that live on {@link GradleRunner}.
 */
class GradleRunnerSpec extends Specification {
    List<URL> classpath
    List<URL> windowsClasspath

    def setup() {
        def userDir = new File(StandardSystemProperty.USER_DIR.value())
        // Partial real-world classpath from a IntegrationSpec launch, only the userDir paths matter, otherwise these are just string comparisons
        def classpathUris = ["file:/Applications/IntelliJ%20IDEA%2015%20EAP.app/Contents/lib/serviceMessages.jar",
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
        "file:$userDir/build/classes/test/",
        "file:$userDir/build/classes/main/",
        "file:$userDir/build/resources/test/",
        "file:$userDir/build/resources/main/",
        "file:/Users/dannyt/.gradle/caches/modules-2/files-2.1/org.spockframework/spock-core/1.0-groovy-2.3/762fbf6c5f24baabf9addcf9cf3647151791f7eb/spock-core-1.0-groovy-2.3.jar",
        "file:/Users/dannyt/.gradle/caches/modules-2/files-2.1/cglib/cglib-nodep/2.2.2/d456bb230c70c0b95c76fb28e429d42f275941/cglib-nodep-2.2.2.jar",
        "file:/Users/dannyt/.gradle/caches/modules-2/files-2.1/commons-lang/commons-lang/2.6/ce1edb914c94ebc388f086c6827e8bdeec71ac2/commons-lang-2.6.jar",
        "file:/Users/dannyt/.gradle/caches/modules-2/files-2.1/junit/junit/4.12/2973d150c0dc1fefe998f834810d68f278ea58ec/junit-4.12.jar",
        "file:/Users/dannyt/.gradle/caches/modules-2/files-2.1/org.hamcrest/hamcrest-core/1.3/42a25dc3219429f0e5d060061f71acb49bf010a0/hamcrest-core-1.3.jar",
        "file:/Users/dannyt/.gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/gradle-core-2.2.1.jar",
        "file:/Users/dannyt/.gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/groovy-all-2.3.6.jar",
        "file:/Users/dannyt/.gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/asm-all-5.0.3.jar",
        "file:/Users/dannyt/.gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/ant-1.9.3.jar",
        "file:/Users/dannyt/.gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/commons-collections-3.2.1.jar",
        "file:/Users/dannyt/.gradle/wrapper/dists/gradle-2.2.1-bin/3rn023ng4778ktj66tonmgpbv/gradle-2.2.1/lib/commons-io-1.4.jar"]
        classpath = classpathUris.collect { new URI(it).toURL() }

        def classpathUrisInWindows = ["file:/C:/Users/tolkv/AppData/Local/JetBrains/Toolbox/apps/IDEA-U/ch-0/162.426.1/lib/idea_rt.jar",
        "file:/C:/Users/tolkv/AppData/Local/JetBrains/Toolbox/apps/IDEA-U/ch-0/162.426.1/plugins/junit/lib/junit-rt.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/charsets.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/deploy.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/access-bridge-64.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/cldrdata.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/dnsns.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/jaccess.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/jfxrt.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/localedata.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/nashorn.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/sunec.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/sunjce_provider.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/sunmscapi.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/sunpkcs11.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/ext/zipfs.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/javaws.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/jce.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/jfr.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/jfxswt.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/jsse.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/management-agent.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/plugin.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/resources.jar",
        "file:/C:/Program%20Files/Java/jdk1.8.0_66/jre/lib/rt.jar",
        "file:/D:/tmp/huntbugs-gradle-plugin/build/classes/test/",
        "file:/D:/tmp/huntbugs-gradle-plugin/build/classes/main/",
        "file:/D:/tmp/huntbugs-gradle-plugin/build/resources/main/",
        "file:/D:/Users/dannyt/.gradle/caches/modules-2/files-2.1/org.spockframework/spock-core/1.0-groovy-2.3/762fbf6c5f24baabf9addcf9cf3647151791f7eb/spock-core-1.0-groovy-2.3.jar",
        "file:/D:/Users/dannyt/.gradle/caches/modules-2/files-2.1/cglib/cglib-nodep/2.2.2/d456bb230c70c0b95c76fb28e429d42f275941/cglib-nodep-2.2.2.jar",
        "file:/D:/Users/dannyt/.gradle/caches/modules-2/files-2.1/commons-lang/commons-lang/2.6/ce1edb914c94ebc388f086c6827e8bdeec71ac2/commons-lang-2.6.jar",
        "file:/D:/Users/dannyt/.gradle/caches/modules-2/files-2.1/junit/junit/4.12/2973d150c0dc1fefe998f834810d68f278ea58ec/junit-4.12.jar",
        "file:/D:/Users/dannyt/.gradle/caches/modules-2/files-2.1/org.hamcrest/hamcrest-core/1.3/42a25dc3219429f0e5d060061f71acb49bf010a0/hamcrest-core-1.3.jar",
        "file:/C:/Users/tolkv/.gradle/wrapper/dists/gradle-2.13-all/1b9wlm73elu4cqnbc0rk7r7qn/gradle-2.13/lib/groovy-all-2.4.4.jar",
        "file:/C:/Users/tolkv/.gradle/wrapper/dists/gradle-2.13-all/1b9wlm73elu4cqnbc0rk7r7qn/gradle-2.13/lib/plugins/gradle-tooling-api-builders-2.13.jar"]
        windowsClasspath = classpathUrisInWindows.collect { new URI(it).toURL() }
    }

    def 'gradle distribution predicate includes expected files'() {
        expect:
        def filtered = FluentIterable.from(classpath).filter(GradleRunner.CLASSPATH_GRADLE_CACHE).toList()
        filtered.size() == 5
    }

    def 'jvm predicate includes expected files'() {
        expect:
        def filtered = FluentIterable.from(classpath).filter(GradleRunner.CLASSPATH_USER_DIR).toList()
        filtered.size() == 4
    }

    def 'default classpath includes only application class paths and dependencies'() {
        expect:
        def filtered = FluentIterable.from(classpath).filter(GradleRunner.CLASSPATH_DEFAULT).toList()
        filtered.size() == 9
    }

    def 'gradle distribution predicate includes expected files in windows'() {
        expect:
        def filtered = FluentIterable.from(windowsClasspath).filter(GradleRunner.CLASSPATH_GRADLE_CACHE).toList()
        filtered.size() == 5
    }

    def 'jvm predicate includes expected files in windows'() {
        given:
        def predicate = new ClasspathUserDirPredicate({ return new File('/D:/tmp/huntbugs-gradle-plugin') })
        expect:
        def filtered = FluentIterable.from(windowsClasspath).filter(predicate).toList()
        filtered.size() == 3
    }

}
