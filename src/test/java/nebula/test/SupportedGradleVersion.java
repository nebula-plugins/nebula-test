package nebula.test;

public enum SupportedGradleVersion {
    MIN("8.12.1"), MAX("9.1.0");
    public final String version;
    SupportedGradleVersion(String version) {
        this.version = version;
    }
}
