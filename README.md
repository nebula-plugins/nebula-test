Nebula Test
===========
Classes specific to testing a Gradle project, leveraging [Spock](http://spockframework.org)

ProjectSpec
-----------
Uses Project Builder to create a in-memory expression of a Gradle build (project variable), specifically in a 'projectDir'. A sanitized project name will
be stored in canonicalName. Caveat, this is ONLY setting up the Project data structure, and not running through the completely lifecycle, Like to
see http://issues.gradle.org/browse/GRADLE-1619.  Its value lays in being able to execute method with a proper Project object, which can flush out most
groovy functions, finding basic compiler like issues. The private method evaluate() can be called on the project variable for force evaluation of
afterEvaluate blocks, keeping in mind that still won't generate a task gradle or run the tasks.

Example:

    package nebula.example

    import java.util.concurrent.atomic.AtomicBoolean

    class ConcreteProjectSpec extends ProjectSpec {
        def 'has Project'() {
            expect:
            project != null
        }

        def 'can evaluate'() {
            setup:
            def signal = new AtomicBoolean(false)
            project.afterEvaluate {
                signal.getAndSet(true)
            }
            when:
            project.evaluate()

            then:
            noExceptionThrown()
            signal.get() == true
        }
    }

PluginProjectSpec
-----------------
Small abstraction over ProjectSpec for plugins, adds three tests that ensure the plugin can be applied properly (idempotently and in a multi-project).

Example:

    package nebula.example

    class PluginProjectExampleSpec extends PluginProjectSpec {
        @Override
        String getPluginName() { return 'plugin-example' }
    }

IntegrationSpec
---------------
Orchestrate a Gradle build via GradleLauncher, which is deprecated, or the Tooling API to perform a high-level integration test of a
project. Each test gets it's own test directory, called *projectDir*. It is up to the implementer to add contents to the *buildFile*
 and the *settingsFile*. The project's name is available as *moduleName*, which is a sanitized version of the test's name.

The spec will assume the Tooling API, but this can be changed by setting *useToolingApi* to false in which case the GradleLauncher will
be used. It's risky to use since it's not support, but it provide more details from the resulting build. Though if you're checking the
contents of the *projectDir* after build, it shouldn't matter. The GradleLauncher is required to debug the running of your build, you
can't set break points in the build.gradle file, but you can set them in the plugins being called.

It's up to your test to call the runTask methods. There are a few utility methods to help assemble a project.

#### Behaviour
|  |                                                                                                                                                                                                                                                      |
|-----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `logLevel`  | Adjust log level being used                                                                                                                                                                                                                                                        |
| `fork`      | By default tests are executed in the same JVM as Gradle which is helpful for debugging your code. If you want classloader isolation, you might want to rather go with a forked JVM for executing your tests. Note: This flag is based on an property from Gradle’s non-public API. |

#### Setup of project
|                                       |                                                                                                                       |
|-----------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------|
| `File directory(String path)`                             | Create a directory, with a mkdirs.                                                                                    |
| `File createFile(String path)`                            | Create a file, relative from the project, with parent directories being created.                                      |
| `def writeHelloWorld(String packageDotted)`               | Write out a simple java HelloWorld in the package provided                                                            |
| `String copyResources(String srcDir, String destination)` | Copy a resource from the classpath to the project’s directory                                                         |
| `String applyPlugin(Class pluginClass)`                   | Returns the appropriate string for applying a plugin, using a loadClass call. Would need to be added to the buildFile |



#### Execution
|                                                                                                          |                                                                          |
|----------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| `BuildResult analyze(String… tasks)`  | Analysis of project with the given tasks, doesn’t actually execute tasks |
| `BuildResult runTasksWithSuccessfully(String… tasks)`                                                    | Run, and assume that the build will succeed.                             |
| `BuildResult runTasksWithFailure(String… tasks)`                                                         | Run, and assume that the build will fail.                                |

#### Validate project after execution
|                                   |                                               |
|-----------------------------------|-----------------------------------------------|
| `boolean fileExists(String path)` | Says if a file was created in the project dir |

#### Create subprojects
|                                                                        |                                                                  |
|------------------------------------------------------------------------|------------------------------------------------------------------|
| `File addSubproject(String subprojectName)`                            | Create a subproject, return back the new directory               |
| `File addSubproject(String subprojectName, String subBuildGradleText)` | Create a subproject setting its build.gradle to the given String |

#### ExecutionResult provides a few useful methods to test the outcome of a build
|                                        |                                                   |
|----------------------------------------|---------------------------------------------------|
| `boolean wasExecuted(String taskPath)` | Says if a task was executed.                      |
| `boolean wasUpToDate(String taskPath)` | Says if a task was recorded as UP-TO-DATE.        |
| `String getStandardError()`            | Returns System.err which can be inspected         |
| `String getStandardOutput()`           | Returns System.out which can be inspected         |
| `Throwable getFailure()`               | Returns the Throwable available to failed builds. |


Example:

    package nebula.example

    import nebula.test.functional.ExecutionResult

    class ConcreteIntegrationSpec extends IntegrationSpec {
        def 'runs build'() {
            when:
            ExecutionResult result = runTasks('dependencies')

            then:
            result.failure == null
        }

        def 'setup and run build'() {
            writeHelloWorld('nebula.hello')
            buildFile << '''
                apply plugin: 'java'
            '''.stripIndent()

            when:
            ExecutionResult result = runTasksSuccessfully('build')

            then:
            fileExists('build/classes/main/nebula/hello/HelloWorld.class')
            result.standardOutput.contains(':compileTestJava')
        }
    }

Generating Test Maven and Ivy Repos
-----------------------------------
More detailed information can be found on our [wiki](https://github.com/nebula-plugins/nebula-test/wiki/Maven-and-Ivy-Test-Repository-Generation).

Caveats:
* this will not check whether the dependency graph you describe is valid
* all listed dependencies will be in the runtime scope of the generated library

### Describing a Dependency Graph (String based)

*Simple Library with no Dependencies*

If i want to create a fake library with group: `test.example`, artifactName: `foo`, and version: `1.0.0`

    String myGraph = 'test.example:foo:1.0.0'

*Library with One Dependency*

To have `test.example:foo:1.0.0` depend on the most recent version in the `1.+` series of `bar:baz`

    String myGraph = 'test.example:foo:1.0.0 -> bar:baz:1.+'

*Library with Multiple Dependencies*

To have `test.example:foo:1.0.0` depend on `bar:baz:1.+`, `g:a:[1.0.0,2.0.0)`, and `g1:a1:3.1.2`

    String myGraph = 'test.example:foo:1.0.0 -> bar:baz:1.+|g:a:[1.0.0,2.0.0)|g1:a1:3.1.2'

### Describing a Dependency Graph (Builder based)

    def builder = new DependencyGraphBuilder()

    DependencyGraph graph = builder.build()

*Simple Library with no Dependencies*

If i want to create a fake library with group: `test.example`, artifactName: `foo`, and version: `1.0.0`

    builder.addModule('test.example:foo:1.0.0')
    //or
    builder.addModule('test.example', 'foo', '1.0.0')
    //or
    def module = new ModuleBuilder('test.example:foo:1.0.0').build()
    builder.addModule(module)

*Library with One Dependency*

To have `test.example:foo:1.0.0` depend on the most recent version in the `1.+` series of `bar:baz`

    def module = new ModuleBuilder('test.example:foo:1.0.0').addDependency('bar:baz:1.+').build()
    builder.addModule(module)

*Library with Multiple Dependencies*

To have `test.example:foo:1.0.0` depend on `bar:baz:1.+`, `g:a:[1.0.0,2.0.0)`, and `g1:a1:3.1.2`

    def module = new ModuleBuilder('test.example:foo:1.0.0')
            .addDependency('bar:baz:1.+')
            .addDependency('g:a:[1.0.0,2.0.0)').build()
    builder.addModule(module)

#### Creating the Graph

*String based syntax*

    import nebula.test.dependencies.DependencyGraph
    def graph = new DependencyGraph(['g:a:1.0.0', 'g1:a1:0.9.0 -> g:a:1.+'])
    // or
    def graph = new DependencyGraph('g:a:1.0.0', 'g1:a1:0.9.0 -> g:a:1.+')

*Builder based syntax*

    import nebula.test.dependencies.DependencyGraphBuilder
    import nebula.test.dependencies.ModuleBuilder

    def graph = new DependencyGraphBuilder().addModule('g0:a0:0.0.1')
            .addModule('g1', 'a1', '1.0.1')
            .addModule(new ModuleBuilder('g2:a2:2.0.1').build())
            .addModule(new ModuleBuilder('g3', 'a3', '3.0.1')
                    .addDependency('g4:a4:4.0.1')
                    .addDependency('g5', 'a5', '5.0.1').build()
            ).build()

### Generating A Repository

Given

    def generator = new GradleDependencyGenerator(graph)

* ivy repos will be at: generator.ivyRepoDir
* maven repos will be at: generator.mavenRepoDir

Code example:

    import nebula.test.dependencies.GradleDependencyGenerator
    def graph = new DependencyGraph(['g:a:1.0.0', 'g1:a1:0.9.0 -> g:a:1.+'])
    def generator = new GradleDependencyGenerator(graph)
    // or to specify directory of repos
    def generator = new GradleDependencyGenerator(graph, 'build/testrepos')

To create a maven repo

    File mavenrepo = generator.generateTestMavenRepo()

To create an ivy repo

    File ivyrepo = generator.generateTestIvyRepo()

Multi-project Helpers
---------------------

### MultiProjectHelper

MultiProjectHelper can create various sub-projects using the ProjectBuilder.

#### Usage for MultiProjectHelper:

    def helper = new MultiProjectHelper(project)
    Project sub = helper.addSubproject('sub')

### MultiProjectIntegrationHelper

MultiProjectIntegrationHelper can create sub-projects using our IntegrationSpec.

#### Usage for MultiProjectIntegrationHelper:

    class MySpec extends IntegrationSpec {
        def helper = new MultiProjectIntegrationHelper(projectDir, setingsFile)

        def 'my test method'() {
            File subDirectory = helper.addSubproject('sub1')
        }
    }

Caveat
------
* This would have been in nebula-core, but via POMs you can't get dependencies just for tests.
* TODO Add links to Javadoc

LICENSE
-------

Copyright 2014 Netflix, Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
