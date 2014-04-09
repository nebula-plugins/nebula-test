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

```
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
```

PluginProjectSpec
-----------------
Small abstraction over ProjectSpec for plugins, adds three tests that ensure the plugin can be applied properly (idempotently and in a multi-project).

Example:

```
package nebula.test

class PluginProjectExampleSpec extends PluginProjectSpec {
    @Override
    String getPluginName() { return 'plugin-example' }
}
```

IntegrationSpec
---------------
Orchastrate a Gradle build via GradleLauncher, which is deprecated. It's risky to use, but it full flushes out a full of a build the *projectDir* 
directory. This class will instantiate a directory with a proper settings.gradle file, you'll have to provide a build.gradle file, by appending
to the _buildFile_ variable. It's up to your test to call the launcher method. There are a few utility methods to help assemble a 

* Behavior - override to change behavior
  * getLogLevel() - Adjust log level being used
  * getAllowedPackages() - Gradle will hide some classes in the classpath, this method would let certain packages to flow through
  * getAllowResources() - Like getAllowedPackages(), but this is specific to resources. Details can be found in the AllowListener class.
* Setup of project
  * _File directory(String path)_ - Create a directory, with a mkdirs.
  * _File createFile(String path)_ - Create a file, relative from the project, with parent directories being created.
  * _def writeHelloWorld(String packageDotted)_ - Write out a simple java HelloWorld in the package provided
  * _String copyResources(String srcDir, String destination)_ - Copy a resource from the classpath to the project's directory
  * _String applyPlugin(Class pluginClass)_ - Returns the appropriate string for applying a plugin, using a loadClass call. Would need to be added to the buildFile
* Check - to validate project
  * _boolean fileExists(String path)_ - Says if a file was created in the project dir
  * _ExecutedTask task(String name)_ - Returns the execute state of task which was run. From ExecutedTask, you can inspect the Task or the TaskStateInternal
  * _Collection<ExecutedTask> tasks(String... names)_ - State of all named tasks
  * _boolean wasExecuted(String taskPath)_ - Says if a task was executed.
  * _boolean wasUpToDate(String taskPath)_ - Says if a task was recorded as UP-TO-DATE.
  * _String getStandardError()_ - Returns System.err, which can be inspected
  * _String getStandardOutput()_ - Returns System.err, which can be inspected
* Execution - actual run a project, only one should be run per test, otherwise the task list will be overwritten.
  * _BuildResult analyze(String... tasks)_ - Analysis of project with the given tasks, doesn't actually execute tasks
  * _BuildResult runTasksWithSuccessfully(String... tasks)_ - Run, and assume that the build will succeed.
  * _BuildResult runTasksWithFailure(String... tasks)_ - Run, and assume that the build will fail.

Example:
```
package nebula.test

import org.gradle.BuildResult

class ConcreteIntegrationSpec extends IntegrationSpec {
    def 'runs build'() {
        when:
        BuildResult buildResult = runTasks('dependencies')

        then:
        buildResult.failure == null
        buildResult.gradle != null
    }

    def 'setup and run build'() {
        writeHelloWorld('nebula.hello')
        buildFile << '''
            apply plugin: 'java'
        '''.stripIndent()

        when:
        runTasksSuccessfully('build')

        then:
        fileExists('build/classes/main/nebula/hello/HelloWorld.class')
        getStandardOutput().contains(':compileTestJava')
    }
}
```

Generating Test Maven and Ivy Repos
-----------------------------------
More detailed information can be found on our [wiki](). 

Caveats:
* this will not check whether the dependency graph you describe is valid
* all listed dependencies will be in the runtime scope of the generated library

### Describing a Dependency Graph

*Simple Library with no Dependencies*
If i want to create a fake library with group: 'test.example', artifactName: 'foo', and version: '1.0.0'

    String myGraph = 'test.example:foo:1.0.0'

*Library with One Dependency*
To have test.example:foo:1.0.0 depend on the most recent version in the 1.+ series of bar:baz

    String myGraph = 'test.example:foo:1.0.0 -> bar:baz:1.+'

*Library with Multiple Dependencies*
To have test.example:foo:1.0.0 depend on bar:baz:1.+, g:a:[1.0.0,2.0.0), and g1:a1:3.1.2

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
