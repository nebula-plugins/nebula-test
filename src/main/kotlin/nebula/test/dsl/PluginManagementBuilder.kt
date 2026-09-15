package nebula.test.dsl

@NebulaTestKitDsl
class PluginManagementBuilder {
    private val plugins = PluginsBuilder()
    private val repositoriesBuilder = RepositoriesBuilder()
    private val includedBuilds: MutableSet<SubProject> = HashSet()

    /**
     * Apply settings plugins
     * @return plugins DSL builder
     */
    fun plugins(): PluginsBuilder {
        return plugins
    }

    /**
     * Apply settings plugins
     * @return plugins DSL builder
     */
    fun plugins(dsl: PluginsBuilder.() -> Unit) {
        plugins.dsl()
    }

    /**
     * Configure project plugin management
     * @return repositories DSL builder
     */
    fun repositories(): RepositoriesBuilder {
        return repositoriesBuilder
    }

    /**
     * Configure project plugin management
     * @return repositories DSL builder
     */
    fun repositories(dsl: RepositoriesBuilder.() -> Unit) {
        repositoriesBuilder.dsl()
    }

    /**
     * add a build to this build as a [composite build](https://docs.gradle.org/current/userguide/composite_builds.html)
     * include statement along with a projectDir override.
     * This includes the build to be available to provide plugins to the buildscript
     * This is invoked automatically when adding projects via [TestProjectBuilder.includedPluginBuild]
     * @param name the name of the project to include
     */
    fun includeBuild(name: String, relativePath: String?) {
        includedBuilds.add(SubProject(name, relativePath))
    }

    fun includeBuild(name: String) {
        includedBuilds.add(SubProject(name, null))
    }

    internal fun hasContent(): Boolean {
        return repositoriesBuilder.hasContent() || plugins.hasContent() || includedBuilds.isNotEmpty()
    }

    fun build(language: BuildscriptLanguage): String {
        val buildFileText = buildString {
            if (hasContent()) {
                append("pluginManagement {").append("\n")
                append(repositoriesBuilder.build(language, 4))
                append(plugins.build(language, 4))
                includedBuilds.forEach { includedBuild: SubProject? ->
                    if (language == BuildscriptLanguage.KOTLIN) {
                        append("    includeBuild(\"").append(includedBuild!!.name).append("\")\n")
                    } else if (language == BuildscriptLanguage.GROOVY) {
                        append("    includeBuild '").append(includedBuild!!.name).append("'\n")
                    }
                }
                append("}\n")
            }
        }
        return buildFileText
    }
}
