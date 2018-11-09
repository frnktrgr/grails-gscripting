package grails.plugin.gscripting.dsl

interface IContext {

	abstract void init(Map callParams, Map state, Map metadata)
	abstract void reset()
	abstract IContext createSharedContext()

}
