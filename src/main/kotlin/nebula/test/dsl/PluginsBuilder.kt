package nebula.test.dsl

@NebulaTestKitDsl
class PluginsBuilder {
    private val plugins: MutableList<Plugin> = mutableListOf()

    /**
     * add a plugin by id
     *
     * @param id the string ID of a plugin
     * @return a {@link Plugin} reference to optionally use to set a plugin version
     */
    fun id(id: String): Plugin {
        val plugin = Plugin(id)
        plugins.add(plugin)
        return plugin
    }

    /**
     * Adds the java plugin
     */
    fun java() {
        id("java").builtIn("java")
    }

    /**
     * Adds a kotlin plugin
     */

    fun kotlin(platform: String): Plugin {
        return id("org.jetbrains.kotlin.$platform")
            .builtIn("kotlin")
            .builtInParam(platform)
    }

    /**
     * Adds kotlin-dsl plugin
     */
    fun kotlinDsl(): Plugin {
        return id("org.gradle.kotlin.kotlin-dsl").builtIn("kotlin-dsl")
    }

    internal fun hasContent(): Boolean {
        return plugins.isNotEmpty()
    }

    fun build(language: BuildscriptLanguage, indentation: Int): String {
        return buildString {
            if (hasContent()) {
                append(" ".repeat(indentation)).append("plugins {\n")
                plugins.forEach { plugin ->
                    append(" ".repeat(indentation + 4)).append(plugin.render(language)).append("\n")
                }
                append(" ".repeat(indentation)).append("}\n")
            }
        }
    }
}
