package grails.plugin.gscripting

import grails.plugin.gscripting.dsl.IContext;
import grails.plugin.gscripting.dsl.IDslProvider;

class ScriptRuntimeEnv {

	def gscriptingService
	String qualifiedName
	String sourcecode
	IDslProvider dslProvider
	IContext ctx
	def instances
	
	Script script
	Boolean locked = false
	
	public ScriptRuntimeEnv(def gscriptingService, String qualifiedName, String sourcecode, IDslProvider dslProvider, IContext ctx) {
		this.gscriptingService = gscriptingService
		this.qualifiedName = qualifiedName
		this.sourcecode = sourcecode
		this.dslProvider = dslProvider
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
				def script = gscriptingService.createScript(qualifiedName, sourcecode, dslProvider, sharedContext)
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
		log.debug "script ${qualifiedName}#${instanceIndex} took: ${endTime-startTime}ms"
		return result
	}
		
}
