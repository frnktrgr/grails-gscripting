grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {
	
    inherits 'global'
    log 'warn'
	
    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    plugins {
        build ':release:3.1.1', ':rest-client-builder:2.1.1' {
            export = false
        }
    }
}
