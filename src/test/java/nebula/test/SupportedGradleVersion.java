package nebula.test;

public enum SupportedGradleVersion {
    MIN("9.0.0"), MAX("9.4.1");
    public final String version;
    SupportedGradleVersion(String version) {
        this.version = version;
    }
}
