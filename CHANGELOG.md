1.12.4 / 2014-09-25
===================

* Improve dependency generation, add builder syntax, add useful helpers

1.12.3 / 2014-09-22
===================

* Add multi-project helpers

1.12.2 / 2014-07-31
===================

* Bug fixes

1.12.1 / 2014-07-31
===================

* Methods to gain more insight into what tasks were run
* Ability to configure if an integration project should clean it self up

1.12.0 / 2014-06-09
===================

* Move to gradle 1.12

1.11.1 / 2014-04-25
===================
* Under lying implementation of IntegrationSpec can now be backed by the Tooling API or a GradleLauncher.
* BREAKING: Return type from runTask* is now ExecutionResult. And depending on the implemention it might not support 
  additional insight, like the Gradle object or the task state.
  
1.9.8 / 2014-04-16
==================
* Added helper methods to IntegrationSpec class

1.9.7 / 2014-04-16
==================
* Bug fixes for writeHelloWorld
* Add ability to specify URI of gradle distribution during generation
* Use contacts plugin

1.9.6 / 2014-04-14
==================
* Ensure that up to date applies to real tasks
* Bug fix to how nebula.test.dependencies parses the -> syntax

1.9.5 / 2014-04-10
==================
* Add nebula.test.dependencies package to allow users to create local ivy and maven repositories

1.9.4 / 2014-04-08
==================
* Improvements to documentation
* Added PluginProjectSpec for people to extend to run some basic tests

1.9.3 / 2014-01-22
==================
* Remove UpToDateCategory

1.9.2 / 2014-01-21
==================
* rev nebula-plugin-plugin to 1.9.5
* stop using dynamic versions of libraries

1.9.1 / 2014-01-17
==================
* improvements to project strucgture
* fix import for FileUtils

1.9.0 / 2014-01-10
==================
* Initial release
