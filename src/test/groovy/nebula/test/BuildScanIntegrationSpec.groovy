package nebula.test

class BuildScanIntegrationSpec extends IntegrationTestKitSpec {
    def setup() {
        new File(projectDir, "settings.gradle") << """
develocity {
    buildScan {
        termsOfUseUrl = 'https://gradle.com/terms-of-service'
        termsOfUseAgree = 'yes'
    }
}
"""
    }

    def 'build scan url is reported in test output'() {
        when:
        def result = runTasks('help', '--scan')

        then:
        result.output.contains("https://gradle.com/s/")
    }
}
