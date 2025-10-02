package nebula.test.dsl;

/**
 * TODO: remove in Java 11+ in favor of built-in functions
 */
class StringUtils {
    private StringUtils() {
    }

    static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
