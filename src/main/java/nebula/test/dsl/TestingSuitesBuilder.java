package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;

import static nebula.test.dsl.StringUtils.repeat;

@NullMarked
@NebulaTestKitDsl
public class TestingSuitesBuilder {
    private final Map<String, JvmTestSuiteBuilder> existingSuites = new HashMap<>();
    private final Map<String, JvmTestSuiteBuilder> createdSuites = new HashMap<>();

    boolean hasContent() {
        return !existingSuites.isEmpty() || !createdSuites.isEmpty();
    }

    public JvmTestSuiteBuilder test() {
        return named("test");
    }

    public JvmTestSuiteBuilder create(String name) {
        return createdSuites.computeIfAbsent(name, (n) -> new JvmTestSuiteBuilder());
    }

    public JvmTestSuiteBuilder named(String name) {
        return existingSuites.computeIfAbsent(name, (n) -> new JvmTestSuiteBuilder());
    }

    String build(BuildscriptLanguage buildscriptLanguage, int baseIndentation) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (hasContent()) {
            stringBuilder.append(repeat(" ", baseIndentation)).append("suites {\n");
            if (!existingSuites.isEmpty()) {
                existingSuites.forEach((key, value) -> {
                    stringBuilder.append(repeat(" ", baseIndentation + 4));
                    switch (buildscriptLanguage) {
                        case GROOVY -> stringBuilder.append(key);
                        case KOTLIN -> stringBuilder.append("named<JvmTestSuite>(\"").append(key).append("\")");
                    }
                    stringBuilder.append(" {\n");
                    stringBuilder.append(value.build(buildscriptLanguage, baseIndentation + 8));
                    stringBuilder.append(repeat(" ", baseIndentation + 4)).append("}\n");
                });
            }
            if (!createdSuites.isEmpty()) {
                createdSuites.forEach((key, value) -> {
                    stringBuilder.append(repeat(" ", baseIndentation + 4));
                    switch (buildscriptLanguage) {
                        case GROOVY -> stringBuilder.append(key);
                        case KOTLIN -> stringBuilder.append("create<JvmTestSuite>(\"").append(key).append("\")");
                    }
                    stringBuilder.append(" {\n");
                    stringBuilder.append(value.build(buildscriptLanguage, baseIndentation + 8));
                    stringBuilder.append(repeat(" ", baseIndentation + 4)).append("}\n");
                });
            }
            stringBuilder.append(repeat(" ", baseIndentation)).append("}\n");
        }
        return stringBuilder.toString();
    }
}
