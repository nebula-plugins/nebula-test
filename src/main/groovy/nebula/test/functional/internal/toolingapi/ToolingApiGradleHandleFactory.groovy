package nebula.test.functional.internal.toolingapi

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import nebula.test.functional.internal.GradleHandle
import nebula.test.functional.internal.GradleHandleBuildListener
import nebula.test.functional.internal.GradleHandleFactory
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.tooling.internal.consumer.DefaultGradleConnector
import org.gradle.tooling.model.build.BuildEnvironment

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@CompileStatic
public class ToolingApiGradleHandleFactory implements GradleHandleFactory {
    public static final String FORK_SYS_PROP = 'nebula.test.functional.fork'
    private static final Map<String, File> gradleHomeDirs = new ConcurrentHashMap<String, File>()

    private final boolean fork
    private final String version
    private final Integer daemonMaxIdleTimeInSeconds

    ToolingApiGradleHandleFactory(boolean fork, String version, Integer daemonMaxIdleTimeInSeconds = null) {
        this.fork = fork
        this.version = version
        this.daemonMaxIdleTimeInSeconds = daemonMaxIdleTimeInSeconds
    }

    @Override
    @CompileDynamic
    public GradleHandle start(File projectDir, List<String> arguments, List<String> jvmArguments = []) {
        GradleConnector connector = createGradleConnector(projectDir)

        boolean forkedProcess = isForkedProcess()

        // Allow for in-process debugging
        connector.embedded(!forkedProcess)

        if (daemonMaxIdleTimeInSeconds != null) {
            connector.daemonMaxIdleTime(daemonMaxIdleTimeInSeconds, TimeUnit.SECONDS)
        }

        ProjectConnection connection = connector.connect()
        BuildLauncher launcher = createBuildLauncher(connection, arguments, jvmArguments)
        createGradleHandle(connection, launcher, forkedProcess)
    }

    private GradleConnector createGradleConnector(File projectDir) {
        GradleConnector connector = GradleConnector.newConnector()
        connector.forProjectDirectory(projectDir)
        configureGradleVersion(connector as DefaultGradleConnector, projectDir)
        connector
    }

    private void configureGradleVersion(DefaultGradleConnector connector, File projectDir) {
        def gradleHomeKey
        if (version != null) {
            gradleHomeKey = version
            connector.useGradleVersion(version)
        } else {
            gradleHomeKey = 'default'
            configureWrapperDistributionIfUsed(connector, projectDir)
        }

        /**
         * TestKit already correctly handles separating the TestKit files from the distribution, but because we're using
         * the tooling API for this legacy case, we need to initialize a connection and interrogate the installation so
         * we can have a separate Gradle home for each test, without causing a distribution download for each test.
         */

        def gradleHomeDir = gradleHomeDirs.computeIfAbsent(gradleHomeKey) {
            def connection = connector.connect()
            try {
                connection.getModel(BuildEnvironment)
                def distribution = getPrivateField(connector, 'distribution')
                def installedDistribution = getPrivateField(distribution, 'installedDistribution')
                getPrivateField(installedDistribution, 'gradleHomeDir')
            } finally {
                connection.close()
            }
        }

        connector.useInstallation(gradleHomeDir)
        connector.useGradleUserHomeDir(new File(projectDir, ".gradle"))
    }

    private static <T> T getPrivateField(Object object, String name) {
        def field = object.getClass().getDeclaredField(name)
        def accessible = field.isAccessible()
        field.setAccessible(true)
        try {
            return field.get(object) as T
        } finally {
            field.setAccessible(accessible)
        }
    }

    private static void configureWrapperDistributionIfUsed(GradleConnector connector, File projectDir) {
        // Search above us, in the project that owns the test
        File target = projectDir.absoluteFile
        while (target != null) {
            URI distribution = prepareDistributionURI(target)
            if (distribution) {
                connector.useDistribution(distribution)
                return
            }
            target = target.parentFile
        }
    }

    // Translated from org.gradle.wrapper.WrapperExecutor to avoid coupling to Gradle API
    private static URI prepareDistributionURI(File target) {
        File propertiesFile = new File(target, "gradle/wrapper/gradle-wrapper.properties")
        if (propertiesFile.exists()) {
            Properties properties = new Properties()
            propertiesFile.withInputStream {
                properties.load(it)
            }
            URI source = new URI(properties.getProperty("distributionUrl"))
            return source.getScheme() == null ? (new File(propertiesFile.getParentFile(), source.getSchemeSpecificPart())).toURI() : source;
        }
        return null
    }

    private boolean isForkedProcess() {
        if (fork) {
            return true
        }

        Boolean.parseBoolean(System.getProperty(FORK_SYS_PROP, Boolean.FALSE.toString()))
    }

    private static BuildLauncher createBuildLauncher(ProjectConnection connection, List<String> arguments, List<String> jvmArguments) {
        BuildLauncher launcher = connection.newBuild()
        launcher.withArguments(arguments as String[])
        launcher.setJvmArguments(jvmArguments as String[])
        launcher
    }

    private GradleHandle createGradleHandle(ProjectConnection connection, BuildLauncher launcher, boolean forkedProcess) {
        GradleHandleBuildListener toolingApiBuildListener = new ToolingApiBuildListener(connection)
        BuildLauncherBackedGradleHandle buildLauncherBackedGradleHandle = new BuildLauncherBackedGradleHandle(launcher, forkedProcess)
        buildLauncherBackedGradleHandle.registerBuildListener(toolingApiBuildListener)
        buildLauncherBackedGradleHandle
    }

    private class ToolingApiBuildListener implements GradleHandleBuildListener {
        private final ProjectConnection connection

        ToolingApiBuildListener(ProjectConnection connection) {
            assert connection != null, 'Requires a non-null connection'
            this.connection = connection
        }

        @Override
        void buildStarted() {}

        @Override
        void buildFinished() {
            connection.close()
        }
    }
}
