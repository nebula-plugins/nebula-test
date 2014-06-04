package nebula.test.functional.internal.toolingapi;

import nebula.test.functional.internal.GradleHandle;
import nebula.test.functional.internal.GradleHandleFactory
import org.gradle.initialization.layout.BuildLayout
import org.gradle.initialization.layout.BuildLayoutFactory;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection
import org.gradle.wrapper.WrapperExecutor;

public class ToolingApiGradleHandleFactory implements GradleHandleFactory {

    public GradleHandle start(File directory, List<String> arguments) {
        GradleConnector connector = GradleConnector.newConnector();
        connector.forProjectDirectory(directory);

        // Try to set distribution, in case a custom distribution is used.
        BuildLayout layout = new BuildLayoutFactory().getLayoutFor(new File('.'), true)
        WrapperExecutor wrapper = WrapperExecutor.forProjectDirectory(layout.rootDirectory, System.out)
        if (wrapper.getDistribution()) {
            connector.useDistribution(wrapper.getDistribution())
        }

        ProjectConnection connection = connector.connect();
        BuildLauncher launcher = connection.newBuild();
        // TODO Deal with connection.close()
        String[] argumentArray = new String[arguments.size()];
        arguments.toArray(argumentArray);
        launcher.withArguments(argumentArray);
        return new BuildLauncherBackedGradleHandle(launcher);
    }
}
