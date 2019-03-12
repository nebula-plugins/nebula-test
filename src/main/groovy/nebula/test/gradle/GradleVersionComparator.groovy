package nebula.test.gradle

import org.gradle.util.GradleVersion

@Category(String)
class GradleVersionComparator {

    boolean versionGreaterThan(String version) {
        return versionCompareTo(this, version) > 0
    }

    boolean versionLessThan(String version) {
        return versionCompareTo(this, version) < 0
    }

    int versionCompareTo(String v1, String v2) {
        return GradleVersion.version(v1).compareTo(GradleVersion.version(v2))
    }
}
