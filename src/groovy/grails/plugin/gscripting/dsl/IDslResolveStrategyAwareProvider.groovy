package grails.plugin.gscripting.dsl

import grails.plugin.gscripting.ScriptRuntimeEnv;

import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation;

interface IDslResolveStrategyAwareProvider extends IDslProvider {

	abstract int getResolveStrategy()

}
