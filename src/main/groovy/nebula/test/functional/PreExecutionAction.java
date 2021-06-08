package nebula.test.functional;

import java.io.File;
import java.util.List;


/**
 * Executes actions before gradle is called.
 */
public interface PreExecutionAction {
  void execute(File projectDir, List<String> arguments, List<String> jvmArguments);
}
