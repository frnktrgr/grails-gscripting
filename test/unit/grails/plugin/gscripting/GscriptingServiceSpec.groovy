package grails.plugin.gscripting

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(GscriptingService)
class GscriptingServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test return value"() {
		expect:
		service.createScriptRuntimeEnv('foo', code).run() == result
		
		where:
		code << ['42/2', 'Math.max(23, 42)', '"foo"', 'process {log.info "test return value"}', 'process { return true }']
		result << [21, 42, 'foo', null, true]
		
    }
	
	void "test script parameters"() {
		expect:
		service.createScriptRuntimeEnv('bar', code).run() == result
		
		where:
		code << ['process([foo:"bar"]){return scriptParams.foo}']
		result << ['bar']
	}
	
	void "test call parameters"() {
		expect:
		service.createScriptRuntimeEnv('bar', 'process {return ctx.callParams.foo}').run(callParams) == result
		
		where:
		callParams << [[foo:'bar']]
		result << ['bar']
	}
	
	void "test stats"() {
		setup:
		def result
		ScriptRuntimeEnv sre = service.createScriptRuntimeEnv("test1", "3+4")
		println sre.stats()
		
		expect:
		sre.stats().first == 0
		sre.stats().last == 0
		sre.stats().runs == 0
		sre.stats().min == 0
		sre.stats().max == 0
		sre.stats().total == 0
		sre.stats().average == 0.0
		
		when:
		sre.run()
		println sre.stats()
		
		then:
		sre.stats().runs == 1
		sre.stats().first != 0
		sre.stats().first == sre.stats().last
		sre.stats().min >= 0
		sre.stats().min == sre.stats().max
		sre.stats().min == sre.stats().average
		sre.stats().min == sre.stats().total
		
		when:
		sre.run()
		println sre.stats()
		
		then:
		sre.stats().runs == 2
		sre.stats().min <= sre.stats().average
		sre.stats().average <= sre.stats().max
		sre.stats().max <= sre.stats().total
	}
	
	void "test execute"() {
		setup:
		def result = [:]
		service.registerScriptRuntimeEnv("foo", "process {println(ctx.callParams.foo+' is going to sleep ...'); sleep(ctx.callParams.time); println(ctx.callParams.foo+' woke up'); return ctx.callParams.time;}")
		def futureTask = service.execute("foo", [foo:"foo", time:5000], [:])
		service.execute("foo", [foo:"bar", time:2000], [:], { result.bar = it.result; println "lol: ${it.result}" })
		result.foo = futureTask.get()?.result
		println "got future task"
		
		expect:
		result.foo == 5000
		result.bar == 2000
	}
}
