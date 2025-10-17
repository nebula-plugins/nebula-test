package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class PluginManagementBuilder {
    private final PluginsBuilder plugins = new PluginsBuilder();
    private final RepositoriesBuilder repositoriesBuilder = new RepositoriesBuilder();

    /**
     * Apply settings plugins
     * @return plugins DSL builder
     */
    public PluginsBuilder plugins() {
        return plugins;
    }

    /**
     * Configure project plugin management
     * @return repositories DSL builder
     */
    public RepositoriesBuilder repositories() {
        return repositoriesBuilder;
    }

    String build(BuildscriptLanguage language) {
        StringBuilder buildFileText = new StringBuilder();
        if (repositoriesBuilder.hasContent() || plugins.hasContent()) {
            buildFileText.append("pluginManagement {").append("\n");
            buildFileText.append(repositoriesBuilder.build(language, 4));
            buildFileText.append(plugins.build(language, 4));
            buildFileText.append("}\n");
        }
        return buildFileText.toString();
    }
}
