package com.netflix.nebula.test.archrules;

import com.netflix.nebula.archrules.core.ArchRulesService;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.Priority;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import java.util.Map;

import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.target;
import static com.tngtech.archunit.core.domain.JavaAccess.Predicates.targetOwner;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackages;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;

public class NebulaTestArchRules implements ArchRulesService {
    static ArchRule noDeprecatedNebulaTest = ArchRuleDefinition.priority(Priority.MEDIUM)
            .noClasses().that(resideOutsideOfPackages("nebula.test.."))
            .should().dependOnClassesThat(resideInAPackage("nebula.test..")
                    .and(annotatedWith(Deprecated.class)))
            .orShould().accessTargetWhere(targetOwner(resideInAPackage("nebula.test.."))
                    .and(target(annotatedWith(Deprecated.class))))
            .allowEmptyShould(true)
            .because("deprecated APIs will be removed in roux 6");

    @Override
    public Map<String, ArchRule> getRules() {
        return Map.of("noDeprecatedNebulaTest", noDeprecatedNebulaTest);
    }
}
