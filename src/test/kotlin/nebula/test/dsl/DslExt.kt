package nebula.test.dsl

fun ProjectBuilder.examplePluginProject(){
    plugins {
        id("java-gradle-plugin")
    }
    rawBuildScript(
        """
gradlePlugin {
    plugins {
        create("myPlugin") {
            id = "org.example.myplugin"
            implementationClass = "org.example.MyPlugin"
        }
    }
}
"""
    )
    src {
        main {
            java(
                "org/example/MyPlugin.java",
                //language=java
                """
package org.example;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
class MyPlugin implements Plugin<Project> {
    public void apply(Project project) {
    
    }
}
"""
            )
        }
    }
}