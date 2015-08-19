package simple

import nebula.test.PluginProjectSpec

class SimplePluginSpec extends PluginProjectSpec {
    String pluginName = 'test.simple-plugin'

    def 'apply creates task'() {
        when:
        project.plugins.apply(SimplePlugin)

        then:
        project.tasks.findByName('sampleTask') != null
    }
}