package nebula.test;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class JavaRuntimeUtil {
    private JavaRuntimeUtil() {
    }

    public static boolean isJwdpLoaded() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        List<String> args = runtime.getInputArguments();
        return args.toString().contains("-agentlib:jdwp");
    }
}
