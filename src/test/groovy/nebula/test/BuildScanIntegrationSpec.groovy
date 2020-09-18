package nebula.test

class BuildScanIntegrationSpec extends IntegrationTestKitSpec {
    def origOut = System.out
    def out = new ByteArrayOutputStream()

    def setup() {
        new File(projectDir, "settings.gradle") << """
gradleEnterprise {
    buildScan {
        termsOfServiceUrl = 'https://gradle.com/terms-of-service'
        termsOfServiceAgree = 'yes'
    }
}
"""
        def printStream = new PrintStream(out)
        System.setOut(printStream)
    }

    def cleanup() {
        System.setOut(origOut)
    }

    def 'build scan url is reported in test output'() {
        when:
        runTasks('help', '--scan')

        then:
        out.toString("UTF-8").contains("Build scan:")
    }
}
