package nebula.test.dsl

import java.util.function.Consumer


@NebulaTestKitDsl
class DependenciesBuilder {
    /**
     * abstraction for project/platform dependencies
     */
    class SpecialDependency(val specialNotation: String)

    private val dependencies: MutableList<String> = mutableListOf()

    fun hasContent(): Boolean {
        return dependencies.isNotEmpty()
    }

    // TODO: once all callers are in kotlin, make this an internal method
    fun rawAdd(notation: String) {
        dependencies.add(notation)
    }

    fun add(configuration: String, notation: String) {
        dependencies.add("""$configuration("$notation")""")
    }

    fun add(configuration: String, dependency: SpecialDependency) {
        dependencies.add("""$configuration(${dependency.specialNotation})""")
    }

    fun project(projectPath: String): SpecialDependency {
        return SpecialDependency("""project("$projectPath")""")
    }

    fun platform(notation: String): SpecialDependency {
        return SpecialDependency("""platform("$notation")""")
    }

    fun implementation(notation: String) {
        add("implementation", notation)
    }

    fun implementation(projectDependency: SpecialDependency) {
        add("implementation", projectDependency)
    }

    fun api(notation: String) {
        add("api", notation)
    }

    fun api(projectDependency: SpecialDependency) {
        add("api", projectDependency)
    }

    fun testImplementation(notation: String) {
        add("testImplementation", notation)
    }

    fun testImplementation(projectDependency: SpecialDependency) {
        add("testImplementation", projectDependency)
    }

    fun classpath(notation: String) {
        add("classpath", notation)
    }

    fun build(language: BuildscriptLanguage, indentation: Int): String {
        return buildString {
            if (dependencies.isNotEmpty()) {
                append(" ".repeat(indentation * 4)).append("dependencies {\n")
                dependencies.forEach(Consumer { dependency: String? ->
                    append(" ".repeat((indentation + 1) * 4)).append(dependency).append("\n")
                })
                append(" ".repeat(indentation * 4)).append("}\n")
            }
        }
    }
}