package nebula.test;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class TestKitOutputUtil {
    private TestKitOutputUtil() {
    }

    public static void checkOutput(String output) {
        outputBuildScan(output);
        checkForMutableProjectState(output);
        checkForDeprecations(output);
    }

    public static void outputBuildScan(String output) {
        AtomicBoolean foundPublishingLine = new AtomicBoolean(false);
        output.lines().forEach(line -> {
            if (foundPublishingLine.get()) {
                if (line.startsWith("http")) {
                    System.out.println("Build scan: $line");
                } else {
                    System.out.println("Build scan was enabled but did not publish: $line");
                }
            }
            if (Objects.equals(line, "Publishing build scan...")) {
                foundPublishingLine.set(true);
            }
        });
    }

    public static void checkForDeprecations(String output) {
        final List<String> deprecations = output.lines().filter(it ->
                it.contains("has been deprecated and is scheduled to be removed in Gradle") ||
                it.contains("Deprecated Gradle features were used in this build") ||
                it.contains("has been deprecated. This is scheduled to be removed in Gradle") ||
                it.contains("This will fail with an error in Gradle") ||
                it.contains("This behaviour has been deprecated and is scheduled to be removed in Gradle")
        ).collect(Collectors.toList());
        // temporary for known issue with overwriting task
        // overridden task expected to not be needed in future version
        if (deprecations.size() == 1 && deprecations.get(0).contains("Creating a custom task named 'dependencyInsight' has been deprecated and is scheduled to be removed in Gradle 5.0.")) {
            return;
        }
        if ((System.getProperty("ignoreDeprecations") == null ||
             !Boolean.parseBoolean(System.getProperty("ignoreDeprecations"))) &&
            !deprecations.isEmpty()) {
            throw new IllegalArgumentException(
                    "Deprecation warnings were found (Set the ignoreDeprecations system property during the test to ignore):\n" +
                    deprecations.stream()
                            .map(it -> " - " + it)
                            .collect(Collectors.joining("\n")));
        }
    }

    static void checkForMutableProjectState(String output) {
        final List<String> mutableProjectStateWarnings = output.lines().filter(it ->
                it.contains("was resolved without accessing the project in a safe manner") ||
                it.contains("This may happen when a configuration is resolved from a thread not managed by Gradle or from a different project") ||
                it.contains("was resolved from a thread not managed by Gradle.") ||
                it.contains("was attempted from a context different than the project context")
        ).collect(Collectors.toList());

        if ((System.getProperty("ignoreMutableProjectStateWarnings") == null ||
             !Boolean.parseBoolean(System.getProperty("ignoreMutableProjectStateWarnings"))) &&
            !mutableProjectStateWarnings.isEmpty()) {
            throw new IllegalArgumentException(
                    "Mutable Project State warnings were found (Set the ignoreMutableProjectStateWarnings system property during the test to ignore):\n" +
                    mutableProjectStateWarnings.stream()
                            .map(it -> " - " + it)
                            .collect(Collectors.joining("\n")));
        }
    }
}
