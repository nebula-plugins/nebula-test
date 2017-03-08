5.1.3 / 2017-03-08
==================

* Fix for multiproject helper in IntegrationTestKitSpec

5.1.2 / 2017-03-03
==================

* make projectdir absolute

5.1.1 / 2017-03-02
==================

* Fix to IntegrationTestKitSpec

5.1.0 / 2017-03-01
==================

* Add IntegrationTestKitSpec -- helper for Gradle TestKit
* Remove a shaded library

5.0.1 / 2017-02-14
==================

* BUGFIX: Fixes for Windows users
 * Thanks Björn Kautler github: Vampire
 * Thanks Willie Slepecki github: scphantm

5.0.0 / 2017-01-04
==================

* Update to Gradle 3.3
* BREAKING CHANGE no longer works with earlier versions of gradle

4.4.3 / 2017-01-03
==================

* Gradle 3.3 updates

4.4.2 / 2016-12-13
==================

* BUGFIX: maven bom generation

4.4.1 / 2016-12-09
==================

4.4.0 / 2016-12-08
==================

4.3.0 / 2016-12-06
==================

* Add maven bom generation helper

4.2.2 / 2016-06-09
==================

4.2.1 / 2016-05-31
==================

4.2.0 / 2016-05-30
==================

* Add objenesis for Spock

4.1.0 / 2016-05-30
==================

* Gradle 2.14 compatibility

4.0.0 / 2015-09-30
==================

* Compatibility with Gradle 2.8

3.1.0 / 2015-09-11
==================

* Added support for Pre-build hooks from ethankhall

3.0.0 / 2015-08-18
==================

* Move to gradle 2.6
* Switch to new publishing

2.2.2 / 2015-06-22
==================

* Add classpath filtering to prevent Gradle, IntelliJ and JVM classes from being added to the IntegrationSpec init script classpath. Set classpathFilter to nebula.test.functional.GradleRunner#CLASSPATH_ALL to restore the default behaviour, or implement your own predicate to control the filtering for your use case

2.2.1 / 2015-03-25
==================

* move to spock 1.0-groovy-2.3
* Improve debugging support for forked tests

2.2.0 / 2015-01-30
===================

* Move to gradle 2.2.1

2.0.2 / 2014-10-24
===================

* Add ability to choose Gradle version

2.0.1 / 2014-09-26
==================

* Merged in dependency generation improvements from 1.12.4

1.12.5 / 2014-10-23
===================

* Add ability to choose Gradle version

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
