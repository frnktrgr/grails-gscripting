package grails.plugin.gscripting.dsl.impl

import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation

import grails.plugin.gscripting.ScriptRuntimeEnv;
import grails.plugin.gscripting.dsl.IContext;
import grails.plugin.gscripting.dsl.IDslProvider;
import grails.plugin.gscripting.dsl.ast.DefaultAstNodeOperation;

class DefaultDslProvider implements IDslProvider {
	
	def grailsApplication
	
	public DefaultDslProvider(def grailsApplication) {
		this.grailsApplication = grailsApplication
	}

	@Override
	public PrimaryClassNodeOperation getAstNodeOperation() {
		return new DefaultAstNodeOperation()
	}

	@Override
	public void addRuntimeConstraints(Object expandoMetaClass) {
//		expandoMetaClass.println = {String msg -> throw new Exception("println is not allowed in this DSL. Please use log instead.")}
	}

	@Override
	public String getHandler() {
		return 'process'
	}

	@Override
	public Object getDslInstance(Map scriptParams, IContext ctx, ScriptRuntimeEnv sre) {
		new DefaultDsl(grailsApplication, scriptParams, ctx, sre)
	}

}
