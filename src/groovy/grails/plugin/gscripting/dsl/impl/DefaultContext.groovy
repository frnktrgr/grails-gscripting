package grails.plugin.gscripting.dsl.impl

import grails.plugin.gscripting.dsl.IContext;

class DefaultContext implements IContext {
	Map callParams = [:]
	Map metadata = [:]
	Map state = [:]
	
	def shared = [:]
	
	public void reset() {
		callParams = [:]
		state = [:]
	}
	
	@Override
	public void init(Map callParams, Map state, Map metadata) {
		this.callParams = callParams
		this.metadata = metadata
		this.state = state?:[:]
	}
	
	@Override
	public synchronized IContext createSharedContext() {
		IContext sharedContext =  new DefaultContext()
		sharedContext.shared = shared
		return sharedContext
	}
}
