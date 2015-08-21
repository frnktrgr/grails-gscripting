package grails.plugin.gscripting.dsl.impl

import grails.plugin.gscripting.dsl.IContext;

class EmptyDsl implements GroovyInterceptable {
	
	Map scriptParams
	IContext ctx

	public EmptyDsl(Map scriptParams, IContext ctx) {
		this.scriptParams = scriptParams
		this.ctx = ctx
	}
	
}
