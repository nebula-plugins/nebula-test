package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class PluginsBuilder {
    private final List<Plugin> plugins = new ArrayList<>();

    /**
     * add a plugin by id
     *
     * @param id the string ID of a plugin
     * @return a {@link Plugin} reference to optionally use to set a plugin version
     */
    public Plugin id(String id) {
        final var plugin = new Plugin(id);
        plugins.add(plugin);
        return plugin;
    }

    /**
     * Adds the java plugin
     */
    public void java() {
        plugins.add(new Plugin("java"));
    }

    String build() {
        final var stringBuilder = new StringBuilder();
        if (!plugins.isEmpty()) {
            stringBuilder.append("plugins {\n");
            for (var plugin : plugins) {
                stringBuilder.append("    ").append(plugin.render()).append("\n");
            }
            stringBuilder.append("}\n");
        }
        return stringBuilder.toString();
    }

    public static class Plugin {
        private final String id;
        @Nullable
        private String version;

        Plugin(String id) {
            this.id = id;
        }

        /**
         * Set the version of the plugin.
         * This only needs to be called for plugins not already on the classpath
         *
         * @param version the version of the plugin
         */
        public void version(String version) {
            this.version = version;
        }

        String render() {
            final var stringBuilder = new StringBuilder();
            stringBuilder.append("id(\"").append(id).append("\")");
            if (version != null) {
                stringBuilder.append(" version (\"").append(version).append("\")");
            }
            return stringBuilder.toString();
        }
    }
}
