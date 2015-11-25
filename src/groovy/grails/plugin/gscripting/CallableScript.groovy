package grails.plugin.gscripting

import java.util.concurrent.Callable;

class CallableScript implements Callable {
	
	ScriptRuntimeEnv scriptRuntimeEnv
	Map callParams
	Map state
	Closure callback
	
	public CallableScript(ScriptRuntimeEnv scriptRuntimeEnv, Map callParams=[:], Map state=[:], Closure callback=null) {
		this.scriptRuntimeEnv = scriptRuntimeEnv;
		this.callParams = callParams
		this.state = state
		this.callback = callback
	}

	@Override
	public Object call() throws Exception {
		def result = scriptRuntimeEnv.run(callParams, state)
		if (callback) {
			callback.call(result)
		}
		return result
	}
	
}