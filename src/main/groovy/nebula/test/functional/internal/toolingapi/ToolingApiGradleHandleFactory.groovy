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
    static final String FORK_SYS_PROP = 'nebula.test.functional.fork'

    public GradleHandle start(File directory, List<String> arguments) {
        GradleConnector connector = createGradleConnector(directory)
        boolean forkedProcess = isForkedProcess()

        // Allow for in-process debugging
        connector.embedded(!forkedProcess)

        ProjectConnection connection = connector.connect();
        BuildLauncher launcher = connection.newBuild();
        String[] argumentArray = new String[arguments.size()];
        arguments.toArray(argumentArray);
        launcher.withArguments(argumentArray);
        createGradleHandle(connection, launcher, forkedProcess)
    }

    private GradleConnector createGradleConnector(File directory) {
        GradleConnector connector = GradleConnector.newConnector()
        connector.forProjectDirectory(directory)

        // Try to set distribution, in case a custom distribution is used.
        BuildLayout layout = new BuildLayoutFactory().getLayoutFor(new File('.'), true)
        WrapperExecutor wrapper = WrapperExecutor.forProjectDirectory(layout.rootDirectory, System.out)

        if (wrapper.getDistribution()) {
            connector.useDistribution(wrapper.getDistribution())
        }

        connector
    }

    private boolean isForkedProcess() {
        Boolean.parseBoolean(System.getProperty(FORK_SYS_PROP, Boolean.FALSE.toString()))
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
