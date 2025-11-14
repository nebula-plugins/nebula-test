package nebula.test;

public enum SupportedGradleVersion {
    MIN("8.14.3"), MAX("9.2.0");
    public final String version;
    SupportedGradleVersion(String version) {
        this.version = version;
    }
}
