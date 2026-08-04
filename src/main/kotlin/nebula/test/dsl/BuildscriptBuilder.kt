package nebula.test.dsl

@NebulaTestKitDsl
class BuildscriptBuilder {
    private val repositoriesBuilder = RepositoriesBuilder()
    private val dependenciesBuilder = DependenciesBuilder()
    fun repositories(): RepositoriesBuilder {
        return repositoriesBuilder
    }

    fun repositories(dsl: RepositoriesBuilder.() -> Unit) {
        dsl(repositoriesBuilder)
    }

    fun dependencies(): DependenciesBuilder {
        return dependenciesBuilder
    }

    fun dependencies(dsl: DependenciesBuilder.() -> Unit) {
        dsl(dependenciesBuilder)
    }

    fun build(language: BuildscriptLanguage, baseIndentation: Int): String {
      return  buildString {
            append("buildscript {\n")
            append(repositoriesBuilder.build(language, baseIndentation+1))
            append(dependenciesBuilder.build(language, baseIndentation+1))
            append("}\n")
        }
    }
}