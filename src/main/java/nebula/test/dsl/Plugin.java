package nebula.test.dsl;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Plugin {
    @Nullable
    private String builtIn;
    @Nullable
    private String builtInParam;
    private final String id;
    @Nullable
    private String version;

    Plugin(String id) {
        this.id = id;
    }

    /**
     * Set the version of the plugin.
     * This only needs to be called for plugins not already on the classpath
     *
     * @param version the version of the plugin
     */
    @NebulaTestKitDsl
    public void version(String version) {
        this.version = version;
    }

    public Plugin builtIn(String builtInName) {
        builtIn = builtInName;
        return this;
    }

    public Plugin builtInParam(String builtInParamName) {
        builtInParam = builtInParamName;
        return this;
    }

    public String render(BuildscriptLanguage language) {
        final StringBuilder stringBuilder = new StringBuilder();

        if (language == BuildscriptLanguage.GROOVY) {
            stringBuilder.append("id '").append(id).append("'");
        } else if (language == BuildscriptLanguage.KOTLIN) {
            if (builtIn != null) {
                if (builtInParam == null) {
                    if (builtIn.contains("-")) {
                        stringBuilder.append("`").append(builtIn).append("`");
                    } else {
                        stringBuilder.append(builtIn);
                    }
                } else {
                    stringBuilder.append(builtIn).append("(\"").append(builtInParam).append("\")");
                }
            } else {
                stringBuilder.append("id(\"").append(id).append("\")");
            }
        }

        if (version != null) {
            if (language == BuildscriptLanguage.GROOVY) {
                stringBuilder.append(" version '").append(version).append("'");
            } else if (language == BuildscriptLanguage.KOTLIN) {
                stringBuilder.append(" version (\"").append(version).append("\")");
            }
        }
        return stringBuilder.toString();
    }
}
