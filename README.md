Nebula Test
===========
Classes specific to testing a Gradle project, leveraging <a href="http://spockframework.org">Spock</a>

ProjectSpec
-----------
Uses Project Builder to create a in-memory expression of a Gradle build (project variable), specifically in a 'projectDir'. A sanitized project name will
be stored in canonicalName. Caveat, this is ONLY setting up the Project data structure, and not running through the completely lifecycle, Like to
see http://issues.gradle.org/browse/GRADLE-1619.  Its value lays in being able to execute method with a proper Project object, which can flush out most 
groovy functions, finding basic compiler like issues. The private method evaluate() can be called on the project variable for force evaluation of
afterEvaluate blocks, keeping in mind that still won't generate a task gradle or run the tasks.

Example:

    package nebula.test

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

    package nebula.test

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
* Behavior - override to change behavior
  * _logLevel_ - Adjust log level being used
  * _useToolingApi_ - Choose to use the tooling api or not
* Setup of project
  * _File directory(String path)_ - Create a directory, with a mkdirs.
  * _File createFile(String path)_ - Create a file, relative from the project, with parent directories being created.
  * _def writeHelloWorld(String packageDotted)_ - Write out a simple java HelloWorld in the package provided
  * _String copyResources(String srcDir, String destination)_ - Copy a resource from the classpath to the project's directory
  * _String applyPlugin(Class pluginClass)_ - Returns the appropriate string for applying a plugin, using a loadClass call. Would need to be added to the buildFile
* Execution - actual run a project, only one should be run per test, otherwise the task list will be overwritten.
  * _BuildResult analyze(String... tasks)_ - Analysis of project with the given tasks, doesn't actually execute tasks
  * _BuildResult runTasksWithSuccessfully(String... tasks)_ - Run, and assume that the build will succeed.
  * _BuildResult runTasksWithFailure(String... tasks)_ - Run, and assume that the build will fail.
* Validate project after execution
  * _boolean fileExists(String path)_ - Says if a file was created in the project dir

BuildResult provides a few useful methods to test the outcome of a build:
  * _boolean wasExecuted(String taskPath)_ - Says if a task was executed.
  * _boolean wasUpToDate(String taskPath)_ - Says if a task was recorded as UP-TO-DATE.
  * _String getStandardError()_ - Returns System.err, which can be inspected
  * _String getStandardOutput()_ - Returns System.out, which can be inspected

Example:

    package nebula.test

    import org.gradle.BuildResult

    class ConcreteIntegrationSpec extends IntegrationSpec {
        def 'runs build'() {
            when:
            BuildResult buildResult = runTasks('dependencies')

            then:
            buildResult.failure == null
        }

        def 'setup and run build'() {
            writeHelloWorld('nebula.hello')
            buildFile << '''
                apply plugin: 'java'
            '''.stripIndent()

            when:
            BuildResult buildResult = runTasksSuccessfully('build')

            then:
            fileExists('build/classes/main/nebula/hello/HelloWorld.class')
            buildResult.standardOutput.contains(':compileTestJava')
        }
    }

Generating Test Maven and Ivy Repos
-----------------------------------
More detailed information can be found on our [wiki](https://github.com/nebula-plugins/nebula-test/wiki/Maven-and-Ivy-Test-Repository-Generation). 

Caveats:
* this will not check whether the dependency graph you describe is valid
* all listed dependencies will be in the runtime scope of the generated library

### Describing a Dependency Graph

*Simple Library with no Dependencies*

If i want to create a fake library with group: `test.example`, artifactName: `foo`, and version: `1.0.0`

    String myGraph = 'test.example:foo:1.0.0'

*Library with One Dependency*

To have `test.example:foo:1.0.0` depend on the most recent version in the `1.+` series of `bar:baz`

    String myGraph = 'test.example:foo:1.0.0 -> bar:baz:1.+'

*Library with Multiple Dependencies*

To have `test.example:foo:1.0.0` depend on `bar:baz:1.+`, `g:a:[1.0.0,2.0.0)`, and `g1:a1:3.1.2`

    String myGraph = 'test.example:foo:1.0.0 -> bar:baz:1.+|g:a:[1.0.0,2.0.0)|g1:a1:3.1.2'

#### Creating the Graph

    import nebula.test.dependencies.DependencyGraph
    def graph = new DependencyGraph(['g:a:1.0.0', 'g1:a1:0.9.0 -> g:a:1.+'])
    // or
    def graph = new DependencyGraph('g:a:1.0.0', 'g1:a1:0.9.0 -> g:a:1.+')

### Generating A Repository

* ivy repos will be at: `<projectdir>/build/testrepogen/ivyrepo`
* maven repos will be at: `<projectdir>/build/testrepogen/mavenrepo`

Code example:

    import nebula.test.dependencies.GradleDependencyGenerator
    def graph = new DependencyGraph(['g:a:1.0.0', 'g1:a1:0.9.0 -> g:a:1.+'])
    def generator = new GradleDependencyGenerator(graph)
    // or to specify directory of repos
    def generator = new GradleDependencyGenerator(graph, 'build/testrepos')

To create a maven repo

    generator.generateTestMavenRepo()

To create an ivy repo

    generator.generateTestIvyRepo()

Caveat
------
* This would have been in nebula-core, but via POMs you can't get dependencies just for tests.
* TODO Add links to Javadoc
