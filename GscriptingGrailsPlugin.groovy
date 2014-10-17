class GscriptingGrailsPlugin {
    def version = "0.0.1"
    def grailsVersion = "2.2 > *"
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]
    def title = "Gscripting Plugin"
    def author = "Frank Tröger"
    def authorEmail = "frnktrgr@gmail.com"
    def description = 'Run Groovy scripts in Grails'
    def documentation = "https://github.com/frnktrgr/grails-gscripting" //"http://grails.org/plugin/gscripting"
    def license = "APACHE"
    def issueManagement = [ system: "GITHUB", url: "https://github.com/frnktrgr/grails-gscripting/issues" ]
    def scm = [ url: "https://github.com/frnktrgr/grails-gscripting" ]
}
