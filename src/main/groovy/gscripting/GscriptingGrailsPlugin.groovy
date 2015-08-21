package gscripting

import grails.plugins.*

class GscriptingGrailsPlugin extends Plugin {
    def grailsVersion = "3.0.0 > *"
    def title = "Gscripting"
    def author = "Frank Tröger"
    def authorEmail = "frnktrgr@gmail.com"
    def description = 'Run Groovy scripts in Grails'
    def profiles = ['web']
    def documentation = "https://github.com/frnktrgr/grails-gscripting" //"http://grails.org/plugin/gscripting"
    def license = "APACHE"
    def issueManagement = [ system: "GITHUB", url: "https://github.com/frnktrgr/grails-gscripting/issues" ]
    def scm = [ url: "https://github.com/frnktrgr/grails-gscripting" ]
}
