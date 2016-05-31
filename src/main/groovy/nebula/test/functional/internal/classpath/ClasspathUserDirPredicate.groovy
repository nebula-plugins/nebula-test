package nebula.test.functional.internal.classpath

import com.google.common.base.Predicate
import com.google.common.base.StandardSystemProperty
import com.google.common.base.Supplier
import groovy.transform.CompileStatic

import java.nio.file.Paths

@CompileStatic
class ClasspathUserDirPredicate implements Predicate<URL> {
    private Supplier<File> userDirSupplier

    ClasspathUserDirPredicate() {
        userDirSupplier = new Supplier<File>() {
            @Override
            File get() {
                return new File(StandardSystemProperty.USER_DIR.value())
            }
        }
    }

    ClasspathUserDirPredicate(Supplier<File> userDirSupplier) {
        if(userDirSupplier == null)
            throw new IllegalArgumentException('userDirSupplier is required')

        this.userDirSupplier = userDirSupplier
    }

    @Override
    boolean apply(URL url) {
        File userDir = userDirSupplier.get()
        return Paths.get(url.toURI()).startsWith(userDir.toPath())
    }
}
