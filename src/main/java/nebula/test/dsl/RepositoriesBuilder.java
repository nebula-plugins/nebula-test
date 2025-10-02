package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static nebula.test.dsl.StringUtils.repeat;

@NullMarked
public class RepositoriesBuilder {
    private final List<Repository> repositories = new ArrayList<>();

    boolean hasContent() {
        return !repositories.isEmpty();
    }

    /**
     * Declare a maven repository using a URL
     *
     * @param url the url of the repository
     */
    @NebulaTestKitDsl
    public void maven(String url) {
        repositories.add(new Maven(url));
    }

    /**
     * The built-in mavenCentral repository
     */
    @NebulaTestKitDsl
    public void mavenCentral() {
        repositories.add(new BuiltIn("mavenCentral()"));
    }

    String build(BuildscriptLanguage buildscriptLanguage, int baseIndentation) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (hasContent()) {
            stringBuilder.append(repeat(" ", baseIndentation)).append("repositories {\n");
            for (Repository repository : repositories) {
                if (repository instanceof BuiltIn) {
                    stringBuilder.append(repeat(" ", baseIndentation)).append("    ").append(((BuiltIn) repository).functionName).append("\n");
                } else if (repository instanceof Maven) {
                    if (buildscriptLanguage == BuildscriptLanguage.GROOVY) {
                        stringBuilder.append(repeat(" ", baseIndentation + 4)).append("maven {").append("\n")
                                .append(repeat(" ", baseIndentation + 8)).append("url = '").append(((Maven) repository).url).append("'").append("\n")
                                .append(repeat(" ", baseIndentation)).append("    }").append("\n");
                    } else if (buildscriptLanguage == BuildscriptLanguage.KOTLIN) {
                        stringBuilder.append(repeat(" ", baseIndentation)).append("    ").append("maven(")
                                .append("url = \"").append(((Maven) repository).url).append("\"").append(")").append("\n");
                    }
                }
            }
            stringBuilder.append(repeat(" ", baseIndentation)).append("}\n");
        }
        return stringBuilder.toString();
    }
}
