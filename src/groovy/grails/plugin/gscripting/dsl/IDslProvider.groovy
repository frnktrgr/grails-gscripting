package grails.plugin.gscripting.dsl

import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation;

interface IDslProvider {

	abstract PrimaryClassNodeOperation getAstNodeOperation()

	abstract void addRuntimeConstraints(expandoMetaClass)
	
	abstract String getHandler()
	
	abstract Object getDslInstance(Map scriptParams, IContext context)
	
}
