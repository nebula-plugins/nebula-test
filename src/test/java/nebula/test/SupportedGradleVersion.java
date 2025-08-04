package nebula.test;

public enum SupportedGradleVersion {
    MIN("8.11.1"), MAX("9.0.0-rc-4");
    public final String version;
    SupportedGradleVersion(String version) {
        this.version = version;
    }
}
