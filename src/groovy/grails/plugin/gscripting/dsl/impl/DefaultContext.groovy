package grails.plugin.gscripting.dsl.impl

import grails.plugin.gscripting.dsl.IContext;

class DefaultContext implements IContext {
	Map callParams = [:]
	Map metadata = [:]
	Map state = new DefaultState()
	
	def shared = [:]
	
	public void reset() {
		callParams = [:]
		state = new DefaultState()
	}
	
	@Override
	public void init(Map callParams, Map state, Map metadata) {
		this.callParams = callParams
		this.metadata = metadata
		this.state = state?:new DefaultState()
	}
	
	@Override
	public synchronized IContext createSharedContext() {
		IContext sharedContext =  new DefaultContext()
		sharedContext.shared = shared
		return sharedContext
	}
}
