package grails.plugin.gscripting.dsl.impl

import org.apache.commons.logging.LogFactory

import grails.plugin.gscripting.ScriptRuntimeEnv;
import grails.plugin.gscripting.dsl.IContext;

class DefaultDsl implements GroovyInterceptable {

	def grailsApplication
	
	Map scriptParams
	IContext ctx
	ScriptRuntimeEnv sre
	
	def log

	def app = [:]
	
	public DefaultDsl(def grailsApplication, Map scriptParams, IContext ctx, ScriptRuntimeEnv sre) {
		this.grailsApplication = grailsApplication
		this.scriptParams = scriptParams
		this.ctx = ctx
		this.sre = sre
		this.log = LogFactory.getLog("grails.plugin.gscripting.script.${ctx.metadata.qualifiedName}".toString());
		grailsApplication.serviceClasses.each {
			def bean = grailsApplication.mainContext.getBean(it.propertyName)
			this.app.put(it.propertyName, grailsApplication.mainContext.getBean(it.propertyName))
		}
	}
	
	@Override
	public Object invokeMethod(String name, Object args) {
//		log.trace "Calling ${name} with ${args} ..."
		def ret
//		def startTime = System.currentTimeMillis()
		def calledMethod = DefaultDsl.metaClass.getMetaMethod(name, args)
		if(!calledMethod) {
			throw new MissingMethodException(name, this.getClass(), args)
		}
		ret = calledMethod?.invoke(this, args)
//		def endTime = System.currentTimeMillis()
//		log.trace "Calling ${name} took ${endTime-startTime}ms"
		return ret
	}
	
	@Override
	public Object getProperty(String name) {
//		this.log.trace "Getting ${name} ..."
		if(name in DefaultDsl.metaClass.properties.name)
			return  DefaultDsl.metaClass.getProperty(this, name)
		if(name in DefaultContext.metaClass.properties.name)
			return DefaultContext.metaClass.getProperty(ctx, name)
		//throw new MissingPropertyException(name, this.getClass());
		ctx.state.get(name)
	}
	
	@Override
	public void setProperty(String name, Object args) {
//		log.trace "Setting ${name} with ${args} ..."
		if(name in DefaultDsl.metaClass.properties.name)
			DefaultDsl.metaClass.setProperty(this, name, args)
		if(name in DefaultContext.metaClass.properties.name)
			DefaultContext.metaClass.setProperty(ctx, name, args)
		ctx.state.put(name,args)
	}
}
