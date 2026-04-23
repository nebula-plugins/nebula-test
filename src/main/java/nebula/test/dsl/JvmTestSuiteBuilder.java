package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static nebula.test.dsl.StringUtils.repeat;

@NullMarked
@NebulaTestKitDsl
public class JvmTestSuiteBuilder {
    @Nullable
    private String framework = null;

    void useJUnitJupiter() {
        framework = "useJUnitJupiter()";
    }

    String build(BuildscriptLanguage buildscriptLanguage, int baseIndentation) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (framework != null) {
            stringBuilder.append(repeat(" ", baseIndentation)).append(framework).append("\n");
        }
        return stringBuilder.toString();
    }
}
