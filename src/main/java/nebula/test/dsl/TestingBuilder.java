package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;

import static nebula.test.dsl.StringUtils.repeat;

@NullMarked
@NebulaTestKitDsl
public class TestingBuilder {
    private final TestingSuitesBuilder suites = new TestingSuitesBuilder();

    public TestingSuitesBuilder suites() {
        return suites;
    }

    boolean hasContent() {
        return suites.hasContent();
    }

    String build(BuildscriptLanguage buildscriptLanguage, int baseIndentation) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (hasContent()) {
            stringBuilder.append(repeat(" ", baseIndentation)).append("testing {\n");
            stringBuilder.append(suites.build(buildscriptLanguage, baseIndentation + 4));
            stringBuilder.append(repeat(" ", baseIndentation)).append("}\n");
        }
        return stringBuilder.toString();
    }
}
