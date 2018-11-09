package grails.plugin.gscripting.dsl.impl

import grails.plugin.gscripting.dsl.IContext;

class DefaultContext implements IContext {
	Map callParams = [:]
	Map state = [:]
	Map metadata = [:]

	def shared = [:]

	@Override
	public void init(Map callParams, Map state, Map metadata) {
		this.callParams = callParams
		this.state = state?:[:]
		this.metadata = metadata
	}

	@Override
	public void reset() {
		callParams = [:]
		state = [:]
		metadata = [:]
	}

	@Override
	public synchronized IContext createSharedContext() {
		IContext sharedContext =  new DefaultContext()
		sharedContext.shared = shared
		return sharedContext
	}

}
