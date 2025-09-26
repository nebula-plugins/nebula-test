package nebula.test;

import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import java.io.FileWriter;
import java.io.IOException;

import static nebula.test.dsl.TestKitAssertions.assertThat;

public class IntegrationTestKitTest extends AbstractIntegrationTestKitBase {

    @BeforeEach
    public void init(TestInfo testInfo) {
        super.initialize(getClass(), testInfo.getDisplayName());
    }

    @AfterEach
    public void cleanup() {
        traitCleanup();
    }

    @Test
    public void testIntegrationTestKit() throws IOException {
        try (var writer = new FileWriter(getBuildFile())) {
            writer.write("""
                plugins {
                    id 'java'
                }
            """);
        }

        var result = runTasks("build");

        assertThat(result)
                .hasNoDeprecationWarnings()
                .hasNoMutableStateWarnings();
        assertThat(result).task(":compileJava").hasOutcome(TaskOutcome.NO_SOURCE);
        assertThat(result).task(":build").hasOutcome(TaskOutcome.SUCCESS);
    }
}
