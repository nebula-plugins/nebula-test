package com.energizedwork.spock.extensions

import groovy.transform.InheritConstructors
import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.extension.AbstractMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification

class TempDirectoryExtension extends AbstractAnnotationDrivenExtension<TempDirectory> {

    @Override
    void visitFieldAnnotation(TempDirectory annotation, FieldInfo field) {
        def interceptor
        if (field.isShared()) {
            interceptor = new SharedTempDirectoryInterceptor(annotation.baseDir(), annotation.clean(), field.name)
        } else {
            interceptor = new TempDirectoryInterceptor(annotation.baseDir(), annotation.clean(), field.name)
        }
        interceptor.install(field.parent.getTopSpec())
    }
}

abstract class DirectoryManagingInterceptor extends AbstractMethodInterceptor {

    protected final String baseDir
    protected final boolean clean
    protected final String fieldName

    DirectoryManagingInterceptor(String baseDir, boolean clean, String fieldName) {
        this.baseDir = baseDir
        this.clean = clean
        this.fieldName = fieldName
    }

    protected final Specification getSpec( IMethodInvocation invocation )
    {
        invocation.instance?:invocation.sharedInstance
    }

    protected void setupDirectory(IMethodInvocation invocation) {
        final specInstance = getSpec(invocation)
        final testName = invocation.feature?invocation.feature.name.replaceAll(/\W+/, '-'):fieldName
        final testDirName = "${ specInstance.class.name }/${testName}"
        File testDir = new File(baseDir, testDirName).canonicalFile

        if (testDir.isDirectory() ) {
            // Creating new directory next to existing one
            for (int counter = 1; testDir.directory; counter++) {
                testDir = new File(baseDir, testDirName + "_$counter").canonicalFile
            }
        }
        assert testDir.with { (!directory) && mkdirs() }, "Failed to create test directory [$testDir]"
        specInstance."$fieldName" = testDir
        assert specInstance."$fieldName" == testDir
    }

    protected void destroyDirectory(invocation) {
        final specInstance = getSpec(invocation)
        File directory = specInstance."$fieldName"

        if (clean) {
            assert (directory.deleteDir() && !directory.isDirectory())
        }
    }

    abstract void install(SpecInfo spec)

}

@InheritConstructors
class TempDirectoryInterceptor extends DirectoryManagingInterceptor {

    @Override
    void interceptSetupMethod(IMethodInvocation invocation) {
        setupDirectory(invocation)
        invocation.proceed()
    }

    @Override
    void interceptCleanupMethod(IMethodInvocation invocation) {
        try {
            invocation.proceed()
        } finally {
            destroyDirectory(invocation)
        }
    }

    @Override
    void install(SpecInfo spec) {
        spec.setupMethod.addInterceptor this
        spec.cleanupMethod.addInterceptor this
    }

}

@InheritConstructors
class SharedTempDirectoryInterceptor extends DirectoryManagingInterceptor {

    @Override
    void interceptSetupSpecMethod(IMethodInvocation invocation) {
        setupDirectory(invocation)
        invocation.proceed()
    }

    @Override
    void interceptCleanupSpecMethod(IMethodInvocation invocation) {
        try {
            invocation.proceed()
        } finally {
            destroyDirectory(invocation)
        }
    }

    @Override
    void install(SpecInfo spec) {
        spec.setupSpecMethod.addInterceptor this
        spec.cleanupSpecMethod.addInterceptor this
    }

}