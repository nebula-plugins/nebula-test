package nebula.test.functional.internal.launcherapi

import nebula.test.functional.internal.GradleHandle
import nebula.test.functional.internal.GradleHandleFactory
import org.gradle.GradleLauncher
import org.gradle.StartParameter
import org.gradle.initialization.DefaultGradleLauncher

/**
 * Created by jryan on 4/22/14.
 */
class LauncherGradleHandleFactory implements GradleHandleFactory {
    @Override
    GradleHandle start(File dir, List<String> arguments) {
        String[] argArr = new String[arguments.size()]
        arguments.toArray(argArr)
        StartParameter startParameter = GradleLauncher.createStartParameter(argArr)
        startParameter.projectDir = dir
        // Relying on arguments to contains all these values which previous were beautifully statically called.
        //        startParameter.buildFile = new File(dir, 'build.gradle')
        //        startParameter.settingsFile = new File(dir, 'settings.gradle')
        //        startParameter.logLevel = LogLevel.INFO
        //        startParameter.showStacktrace = ShowStacktrace.ALWAYS

        DefaultGradleLauncher launcher = GradleLauncher.newInstance(startParameter)

        return new GradleLauncherBackedGradleHandle(launcher);

    }
}
