package nebula.test.functional.internal.toolingapi

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import nebula.test.functional.internal.GradleHandle
import nebula.test.functional.internal.GradleHandleBuildListener
import nebula.test.functional.internal.GradleHandleFactory
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection

@CompileStatic
public class ToolingApiGradleHandleFactory implements GradleHandleFactory {
    public static final String FORK_SYS_PROP = 'nebula.test.functional.fork'

    private final boolean fork
    private final String version

    ToolingApiGradleHandleFactory(boolean fork, String version) {
        this.fork = fork
        this.version = version
    }

    @Override
    @CompileStatic(TypeCheckingMode.SKIP)
    public GradleHandle start(File projectDir, List<String> arguments, List<String> jvmArguments = []) {
        GradleConnector connector = createGradleConnector(projectDir)

        boolean forkedProcess = isForkedProcess()

        // Allow for in-process debugging
        connector.embedded(!forkedProcess)

        ProjectConnection connection = connector.connect();
        BuildLauncher launcher = createBuildLauncher(connection, arguments, jvmArguments)
        createGradleHandle(connection, launcher, forkedProcess)
    }

    private GradleConnector createGradleConnector(File projectDir) {
        GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory(projectDir);
        configureGradleVersion(connector, projectDir)
        connector
    }

    private void configureGradleVersion(GradleConnector connector, File projectDir) {
        if (version != null) {
            connector.useGradleVersion(version)
        } else {
            configureWrapperDistributionIfUsed(connector, projectDir)
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
        BuildLauncher launcher = connection.newBuild();
        launcher.withArguments(arguments as String[]);
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
