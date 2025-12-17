package com.netflix.nebula.test.archrules;

import com.netflix.nebula.archrules.core.Runner;
import com.tngtech.archunit.lang.EvaluationResult;
import nebula.test.Integration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NebulaTestArchRulesTest {

    @Test
    public void testNebulaTestArchRule() {
        EvaluationResult result = Runner.check(NebulaTestArchRules.DEPRECATED, Failing.class);
        assertThat(result.hasViolation()).isTrue();
    }

    static abstract class Failing implements Integration {

    }
}
