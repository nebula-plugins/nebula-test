package nebula.test.functional.internal


import nebula.test.functional.PreExecutionAction
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DefaultGradleRunnerSpec extends Specification {

  @Rule TemporaryFolder temporaryFolder

  def 'will execute actions before run is called'() {
    setup:
    def projectDir = temporaryFolder.newFolder()
    def handleFactory = Mock(GradleHandleFactory)
    def runner = new DefaultGradleRunner(handleFactory)

    when:
    runner.handle(projectDir, ['arg'], ['jvm'], [new WriteFileAction(projectDir)])

    then:
    1 * handleFactory.start(projectDir, ['arg'], ['jvm'])
  }

  static class WriteFileAction implements PreExecutionAction {
    File expectedDir

    WriteFileAction(File expectedDir) {
      this.expectedDir = expectedDir
    }

    @Override
    void execute(File projectDir, List<String> arguments, List<String> jvmArguments) {
      assert expectedDir.absolutePath == projectDir.absolutePath

      assert arguments.size() == 1
      assert arguments.first() == 'arg'

      assert jvmArguments.size() == 1
      assert jvmArguments.first() == 'jvm'
    }
  }
}
