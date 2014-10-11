package grails.plugin.gscripting

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import grails.plugin.gscripting.dsl.ast.DefaultAstNodeOperation;
import grails.plugin.gscripting.dsl.impl.DefaultContext;
import grails.plugin.gscripting.dsl.impl.DefaultDsl;
import grails.plugin.gscripting.dsl.impl.DefaultDslProvider;
import grails.plugin.gscripting.dsl.impl.DefaultState;
import grails.plugin.gscripting.dsl.impl.EmptyDslProvider;
import grails.plugin.gscripting.dsl.IContext
import grails.plugin.gscripting.dsl.IDslProvider;
import groovy.lang.GroovyClassLoader.ClassCollector
import groovy.lang.GroovyClassLoader.InnerLoader

import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilationUnit.PrimaryClassNodeOperation;
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.Phases
import org.codehaus.groovy.control.SourceUnit

class GscriptingService {
	
	def grailsApplication
	
	ConcurrentHashMap<String, IDslProvider> dslProviders = new ConcurrentHashMap<String, Object>()
	ConcurrentHashMap<String, ScriptRuntimeEnv> scriptRuntimeEnvs = new ConcurrentHashMap<String, Object>()
	
	def run(String qualifiedName, Map callParams=[:], Map state=[:]) {
		def result = null
		if (scriptRuntimeEnvs.containsKey(qualifiedName)) {
			result = scriptRuntimeEnvs.get(qualifiedName).run(callParams, state)
		} else {
			log.warn "no script with qualifiedName ${qualifiedName} registered"
		}
		return result
	}
	
	def registerScriptRuntimeEnv(String qualifiedName, String sourcecode, String dslProviderLabel="default", IContext ctx=new DefaultContext()) {
		def scriptRuntimeEnv = createScriptRuntimeEnv(qualifiedName, sourcecode, getDslProvider(dslProviderLabel), ctx)
		scriptRuntimeEnvs.put(qualifiedName, scriptRuntimeEnv)
	}
	
	def unregisterScriptRuntimeEnv(String qualifiedName) {
		scriptRuntimeEnvs.remove(qualifiedName)
	}
	
	def registerDslProvider(String label, Object dslProvider) {
		dslProviders.put(label, dslProvider)
	}
	
	def unregisterDslProvider(String label) {
		dslProviders.remove(label)
	}
	
	def getDslProvider(String label) {
		if (dslProviders.isEmpty()) {
			registerDslProvider("default", new DefaultDslProvider(grailsApplication))
			registerDslProvider("empty", new EmptyDslProvider())
		}
		if (!dslProviders.containsKey(label)) {
			throw new Exception("no dsl provider with label ${label} registered".toString())
		}
		dslProviders.get(label)
	}
	
	def createScriptRuntimeEnv(String label, String sourcecode, IDslProvider dslProvider=new DefaultDslProvider(grailsApplication), IContext ctx=new DefaultContext()) {
		new ScriptRuntimeEnv(this, label, sourcecode, dslProvider, ctx)
	}
	
	Script createScript(String qualifiedName, String sourcecode, IDslProvider dslProvider, IContext ctx) {
		def name = "grails.plugin.gscripting.script.default.${qualifiedName}".toString()
		log.trace "creating script ${name} .."
		def instance = null
		def shell = new GroovyShell(grailsApplication.classLoader)
		Script groovyScript = shell.parse(sourcecode, name)
		initDSL(groovyScript, sourcecode, dslProvider, ctx)
		return groovyScript
	}
	
	void initDSL(groovy.lang.Script groovyScript, String sourcecode, IDslProvider dslProvider, IContext ctx) {
		checkCompiletimeConstraints(sourcecode, dslProvider.getAstNodeOperation())
		// Extend script class
		ExpandoMetaClass emc = new ExpandoMetaClass(groovyScript.class, false)
		emc."${dslProvider.getHandler()}" = { Map scriptParams, Closure cl ->
			cl.delegate = dslProvider?.getDslInstance(scriptParams, ctx)
			cl.resolveStrategy = Closure.OWNER_FIRST
			cl()
		}
		dslProvider.addRuntimeConstraints(emc)
		emc.initialize()
		groovyScript.metaClass = emc
	}
	
	def checkCompiletimeConstraints(String sourcecode, PrimaryClassNodeOperation astNodeOperation) {
		// Transform String to Stream
		def config = new CompilerConfiguration()
		def bytes = sourcecode.getBytes(config.getSourceEncoding());
		def inputStream = new ByteArrayInputStream(bytes)
		// Create CompilationUnit with custom ASTNodeOperation
		def codeSource = new GroovyCodeSource(sourcecode,"temp.groovy", "http://grails.plugin/dsl")
		def classLoader = new GroovyClassLoader()
		CompilationUnit cu = new CompilationUnit(config, codeSource.getCodeSource(), classLoader)
		if(astNodeOperation)
			cu.addPhaseOperation(astNodeOperation, Phases.SEMANTIC_ANALYSIS)
		// Compile
		SourceUnit su = cu.addSource(codeSource.getName(), inputStream);
		ClassCollector collector = new ClassCollector(new InnerLoader(classLoader), cu, su);
		cu.setClassgenCallback(collector);
		cu.compile(Phases.OUTPUT)
	}
	
}