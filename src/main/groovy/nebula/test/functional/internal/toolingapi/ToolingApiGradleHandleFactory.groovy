package nebula.test.functional.internal.toolingapi

import nebula.test.functional.internal.GradleHandle
import nebula.test.functional.internal.GradleHandleBuildListener
import nebula.test.functional.internal.GradleHandleFactory
import org.gradle.initialization.layout.BuildLayout
import org.gradle.initialization.layout.BuildLayoutFactory
import org.gradle.tooling.BuildLauncher
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ProjectConnection
import org.gradle.wrapper.WrapperExecutor

public class ToolingApiGradleHandleFactory implements GradleHandleFactory {

    private final String version

    ToolingApiGradleHandleFactory(String version) {
        this.version = version
    }

    public GradleHandle start(File projectDir, List<String> arguments) {
        GradleConnector connector = createGradleConnector(projectDir)
        ProjectConnection connection = connector.connect();
        BuildLauncher launcher = createBuildLauncher(connection, arguments)
        createGradleHandle(connection, launcher)
    }

    private GradleConnector createGradleConnector(File projectDir) {
        GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory(projectDir);
        configureGradleVersion(connector)
        connector
    }

    private void configureGradleVersion(GradleConnector connector) {
        if (version != null) {
            connector.useGradleVersion(version)
        } else {
            configureWrapperDistributionIfUsed(connector)
        }
    }

    private void configureWrapperDistributionIfUsed(GradleConnector connector) {
        BuildLayout layout = new BuildLayoutFactory().getLayoutFor(new File('.'), true)
        WrapperExecutor wrapper = WrapperExecutor.forProjectDirectory(layout.rootDirectory, System.out)
        if (wrapper.distribution) {
            connector.useDistribution(wrapper.distribution)
        }
    }

    private BuildLauncher createBuildLauncher(ProjectConnection connection, List<String> arguments) {
        BuildLauncher launcher = connection.newBuild();
        String[] argumentArray = new String[arguments.size()];
        arguments.toArray(argumentArray);
        launcher.withArguments(argumentArray);
        launcher
    }

    private GradleHandle createGradleHandle(ProjectConnection connection, BuildLauncher launcher) {
        GradleHandleBuildListener toolingApiBuildListener = new ToolingApiBuildListener(connection)
        BuildLauncherBackedGradleHandle buildLauncherBackedGradleHandle = new BuildLauncherBackedGradleHandle(launcher)
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
