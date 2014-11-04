package grails.plugin.gscripting.dsl.impl

import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation

import grails.plugin.gscripting.ScriptRuntimeEnv;
import grails.plugin.gscripting.dsl.IContext;
import grails.plugin.gscripting.dsl.IDslProvider;

class EmptyDslProvider implements IDslProvider {
	
	public EmptyDslProvider() {
	}

	@Override
	public PrimaryClassNodeOperation getAstNodeOperation() {
		return null
	}

	@Override
	public void addRuntimeConstraints(Object expandoMetaClass) {
	}

	@Override
	public String getHandler() {
		return 'process'
	}

	@Override
	public Object getDslInstance(Map scriptParams, IContext ctx, ScriptRuntimeEnv sre) {
		new EmptyDsl(scriptParams, ctx)
	}

}
