package nebula.test.functional

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.SkipWhenEmpty

class SomeTask extends DefaultTask {

    @SkipWhenEmpty
    @InputDirectory
    File input
}
