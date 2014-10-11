package grails.plugin.gscripting.dsl.impl

import grails.plugin.gscripting.dsl.IContext;

class EmptyDsl implements GroovyInterceptable {
	
	Map scriptParams
	IContext ctx

	public EmptyDsl(Map scriptParams, IContext ctx) {
		this.scriptParams = scriptParams
		this.ctx = ctx
	}
	
	@Override
	public MetaClass getMetaClass() {
		return null;
	}

	@Override
	public Object getProperty(String arg0) {
		return null;
	}

	@Override
	public Object invokeMethod(String arg0, Object arg1) {
		return null;
	}

	@Override
	public void setMetaClass(MetaClass arg0) {

	}

	@Override
	public void setProperty(String arg0, Object arg1) {

	}

}
