package grails.plugin.gscripting.dsl.ast

import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;

class DefaultConstraintVisitor extends CodeVisitorSupport {
	
	def constraintVerbs =['exit']
	
	void visitMethodCallExpression(MethodCallExpression expression) {
		
		ConstantExpression method = expression.getMethod()
		if(constraintVerbs.contains(method.getValue())){
			throw new Exception("gscripting DSL: ${method.getValue()} is not allowed");
		}
		
		super.visitMethodCallExpression(expression)
	}
}
