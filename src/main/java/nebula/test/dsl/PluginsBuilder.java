package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static nebula.test.dsl.StringUtils.repeat;

@NullMarked
public class PluginsBuilder {
    private final List<Plugin> plugins = new ArrayList<>();

    /**
     * add a plugin by id
     *
     * @param id the string ID of a plugin
     * @return a {@link Plugin} reference to optionally use to set a plugin version
     */
    @NebulaTestKitDsl
    public Plugin id(String id) {
        final Plugin plugin = new Plugin(id);
        plugins.add(plugin);
        return plugin;
    }

    /**
     * Adds the java plugin
     */
    @NebulaTestKitDsl
    public void java() {
        plugins.add(new Plugin("java"));
    }

    boolean hasContent() {
        return !plugins.isEmpty();
    }

    String build(BuildscriptLanguage language, int indentation) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (hasContent()) {
            stringBuilder.append(repeat(" ", indentation)).append("plugins {\n");
            for (Plugin plugin : plugins) {
                stringBuilder.append(repeat(" ", indentation + 4)).append(plugin.render(language)).append("\n");
            }
            stringBuilder.append(repeat(" ", indentation)).append("}\n");
        }
        return stringBuilder.toString();
    }

    // TODO support "built-in" plugins like java similar to the repository builtins like mavenCentral()
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
        @NebulaTestKitDsl
        public void version(String version) {
            this.version = version;
        }

        String render(BuildscriptLanguage language) {
            final StringBuilder stringBuilder = new StringBuilder();
            if (language == BuildscriptLanguage.GROOVY) {
                stringBuilder.append("id '").append(id).append("'");
            } else if (language == BuildscriptLanguage.KOTLIN) {
                stringBuilder.append("id(\"").append(id).append("\")");
            }

            if (version != null) {
                if (language == BuildscriptLanguage.GROOVY) {
                    stringBuilder.append(" version '").append(version).append("'");
                } else if (language == BuildscriptLanguage.KOTLIN) {
                    stringBuilder.append(" version (\"").append(version).append("\")");
                }
            }
            return stringBuilder.toString();
        }
    }
}
