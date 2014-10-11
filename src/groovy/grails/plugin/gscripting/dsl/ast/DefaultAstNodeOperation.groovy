package grails.plugin.gscripting.dsl.ast

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation;
import org.codehaus.groovy.control.SourceUnit;

class DefaultAstNodeOperation extends PrimaryClassNodeOperation {

	@Override
	public void call(SourceUnit sourceUnit, GeneratorContext generatorContext, ClassNode classNode)
			throws CompilationFailedException {
		sourceUnit.getAST().getStatementBlock().visit(new DefaultConstraintVisitor())
	}
			
}
