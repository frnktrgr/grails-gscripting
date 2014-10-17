package grails.plugin.gscripting

import grails.plugin.gscripting.dsl.IContext;
import grails.plugin.gscripting.dsl.IDslProvider;

class ScriptRuntimeEnv {

	def gscriptingService
	String qualifiedName
	String sourcecode
	String dslProviderLabel
	IContext ctx
	def instances
	
	Script script
	Boolean locked = false
	
	long first = 0
	long last = 0
	int runs = 0
	long min = 0
	long max = 0
	double average = 0
	
	public ScriptRuntimeEnv(def gscriptingService, String qualifiedName, String sourcecode, String dslProviderLabel, IContext ctx) {
		this.gscriptingService = gscriptingService
		this.qualifiedName = qualifiedName
		this.sourcecode = sourcecode
		this.dslProviderLabel = dslProviderLabel
		this.ctx = ctx
		instances = []
	}
	
	def run(Map callParams=[:], Map state=[:]) {
		def instanceIndex
		def instance = null
		
		// lock this
		synchronized (this) {
			instanceIndex = instances.findIndexOf { !it.locked }
			if (instanceIndex < 0) {
				log.debug "script ${qualifiedName} - creating new instance .."
				def sharedContext = ctx.createSharedContext()
				def script = gscriptingService.createScript(qualifiedName, sourcecode, dslProviderLabel, sharedContext)
				instance = [script: script, ctx: sharedContext, locked: false]
				instances.add(instance)
				instanceIndex = instances.size()-1
				log.debug "script ${qualifiedName} - total instances: ${instances.size()}"
			} else {
				instance = instances[instanceIndex]
			}
			// lock instance
			instance.locked = true
		}
		
		// init ctx
		instance.ctx.init(callParams, state, [qualifiedName: qualifiedName, sourcecode: sourcecode,	instanceIndex: instanceIndex])
		log.debug "running script ${qualifiedName}#${instanceIndex} .."
		def startTime = System.currentTimeMillis()
		def result = null
		try {
			result = instance.script.run()
		} finally {
			// unlock instance
			instance.locked = false
		}
		def endTime = System.currentTimeMillis()
		def duration = endTime-startTime
		updateStats(startTime, duration)
		log.debug "script ${qualifiedName}#${instanceIndex} took: ${duration}ms"
		return result
	}
	
	def updateStats(long startTime, long duration) {
		synchronized (this) {
			runs++
			first = ((!first)||(first>startTime))?startTime:first
			last = last<startTime?startTime:last
			average = average + (duration-average)/runs
			min = ((!min)||(duration<min))?duration:min
			max = duration>max?duration:max
		}
	}
	
	def stats() {
		[first: first, last:last, runs:runs, min:min, max:max, average:average]
	}
		
}
