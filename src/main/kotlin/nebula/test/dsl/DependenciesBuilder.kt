package nebula.test.dsl

import java.util.function.Consumer

@NebulaTestKitDsl
class DependenciesBuilder {
    private val dependencies: MutableList<String> = mutableListOf()

   internal fun rawAdd(notation: String) {
        dependencies.add(notation)
    }

    fun add(configuration: String, notation: String) {
        dependencies.add("""$configuration("$notation")""")
    }

    fun implementation(notation: String) {
        add("implementation", notation)
    }

    fun api(notation: String) {
        add("api", notation)
    }

    fun testImplementation(notation: String) {
        add("testImplementation", notation)
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