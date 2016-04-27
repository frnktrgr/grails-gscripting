package grails.plugin.gscripting

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import grails.plugin.gscripting.dsl.ast.DefaultAstNodeOperation;
import grails.plugin.gscripting.dsl.impl.DefaultContext;
import grails.plugin.gscripting.dsl.impl.DefaultDsl;
import grails.plugin.gscripting.dsl.impl.DefaultDslProvider;
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
	
	ExecutorService executor
	
	def getExecutor() {
		if (executor) {
			return executor
		}
		log.debug "creating new executor cached thread pool ..."
		executor = Executors.newCachedThreadPool()
		return executor
	}
	
	FutureTask execute(String qualifiedName, Map callParams=[:], Map state=[:], Closure callback=null) {
		FutureTask futureTask = null
		if (scriptRuntimeEnvs.containsKey(qualifiedName)) {
			log.debug "run async sre ${qualifiedName}"
			Callable callable = new CallableScript(scriptRuntimeEnvs.get(qualifiedName), callParams, state, callback)
			futureTask = new FutureTask(callable)
			getExecutor().execute(futureTask)
		} else {
			log.warn "no script with qualifiedName ${qualifiedName} registered"
		}
		return futureTask
	}

	def run(String qualifiedName, Map callParams=[:], Map state=[:]) {
		def result = null
		if (scriptRuntimeEnvs.containsKey(qualifiedName)) {
			log.debug "run sre ${qualifiedName}"
			result = scriptRuntimeEnvs.get(qualifiedName).run(callParams, state)
		} else {
			log.warn "no script with qualifiedName ${qualifiedName} registered"
		}
		return result
	}
	
	def registerScriptRuntimeEnv(String qualifiedName, String sourcecode, String dslProviderLabel="default", IContext ctx=new DefaultContext(), Object additionalData=null) {
		log.debug "register sre ${qualifiedName}"
		def scriptRuntimeEnv = createScriptRuntimeEnv(qualifiedName, sourcecode, dslProviderLabel, ctx, additionalData)
		scriptRuntimeEnvs.put(qualifiedName, scriptRuntimeEnv)
	}
	
	def unregisterScriptRuntimeEnv(String qualifiedName) {
		log.debug "unregister sre ${qualifiedName}"
		scriptRuntimeEnvs.remove(qualifiedName)
	}
	
	def registerDslProvider(String label, Object dslProvider) {
		log.debug "register dsl provider ${label}"
		dslProviders.put(label, dslProvider)
	}
	
	def unregisterDslProvider(String label) {
		log.debug "unregister dsl provider ${label}"
		dslProviders.remove(label)
	}
	
	def stats() {
		def stats = [:]
		scriptRuntimeEnvs.keySet().each { qualifiedName ->
			stats[qualifiedName] = scriptRuntimeEnvs.get(qualifiedName)?.stats()
		}
		return stats
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
	
	def createScriptRuntimeEnv(String label, String sourcecode, String dslProviderLabel="default", IContext ctx=new DefaultContext(), Object additionalData=null) {
		new ScriptRuntimeEnv(this, label, sourcecode, dslProviderLabel, ctx, additionalData)
	}
	
	Script createScript(ScriptRuntimeEnv sre, IContext ctx) {
		def name = "grails.plugin.gscripting.script.default.${sre.qualifiedName}".toString()
		log.trace "creating script ${name} .."
		def instance = null
		def shell = new GroovyShell(grailsApplication.classLoader)
		Script groovyScript = shell.parse(sre.sourcecode, name)
		initDSL(groovyScript, sre, ctx)
		return groovyScript
	}
	
	void initDSL(groovy.lang.Script groovyScript, ScriptRuntimeEnv sre, IContext ctx) {
		checkCompiletimeConstraints(sre.sourcecode, getDslProvider(sre.dslProviderLabel)?.getAstNodeOperation())
		// Extend script class
		ExpandoMetaClass emc = new ExpandoMetaClass(groovyScript.class, false)
		emc."${getDslProvider(sre.dslProviderLabel)?.getHandler()}" = { Map scriptParams=[:], Closure cl ->
			cl.delegate = getDslProvider(sre.dslProviderLabel)?.getDslInstance(scriptParams, ctx, sre)
			cl.resolveStrategy = Closure.DELEGATE_FIRST
			cl()
		}
		getDslProvider(sre.dslProviderLabel)?.addRuntimeConstraints(emc)
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
