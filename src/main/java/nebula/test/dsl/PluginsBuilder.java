package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;

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
        id("java").builtIn("java");
    }

    /**
     * Adds a kotlin plugin
     */
    @NebulaTestKitDsl
    public Plugin kotlin(String platform) {
        return id("org.jetbrains.kotlin." + platform).builtIn("kotlin").builtInParam(platform);
    }

    /**
     * Adds kotlin-dsl plugin
     */
    @NebulaTestKitDsl
    public Plugin kotlinDsl() {
        return id("org.gradle.kotlin.kotlin-dsl").builtIn("kotlin-dsl");
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
}
