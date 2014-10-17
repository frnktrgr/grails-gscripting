grails-gscripting
=================

Run Groovy scripts in Grails

##Logging (Config.groovy):
````
debug 'grails.plugin.gscripting',
      'grails.app.services.grails.plugin.gscripting'
````

##Create a script and run it
```groovy
def gscriptingService
def sre = gscriptingService.createScriptRuntimeEnv("Foo", '''
process([c:"hello", d:"world"]) {
  // call another service
	// app.fooService.bar();
	log.info("callParams: "+ctx.callParams);
	log.info("scriptParams: "+scriptParams);
	log.debug("metadata: "+ctx.metadata);  3 + 4 + 2
}
''')
sre.run([a:23, b:42])
sre.run()
```
`gscriptingService.createScriptRuntimeEnv(String label, String sourcecode)` creates a new script with the default DSL provider. In the closure given as an argument to `process(Map scriptParams) { <HERE> }` you can use some DSL properties as described below:
* `log`: logger
* `scriptParams`: the map given as first argument to `process`
* `grailsApplication`: grailsApplication instance like in controllers or services
* `app.<serviceName>`: services of your Grails application, e.g. gscriptingService
* `ctx`: the default context as described below:
  * `ctx.callParams`: the map given as an argument to `run`
  * `ctx.metadata`: a map with `qualifiedName`, `sourcecode`, and `instanceIndex` (see below)
  * `ctx.state`: a map for variables, can also be access directly, e.g. `ctx.state.foo = 42` is the same as `foo = 42`
  * `ctx.shared`: a map shared by every instance of the script (not synchronized)
You can run a script multiple times, once you created it. Simple call `run()` or `run(Map callParams)` on the script. `stats()` returns simple statistics like min/max/average execution time.

##Register script and run by qualified name
```groovy
def gscriptingService
gscriptingService.registerScriptRuntimeEnv("foo.Bar", '''
process([first:"hello", second:"world"]) {
	// call another service
	// app.fooService.bar();
	log.info("callParams: "+ctx.callParams);
	log.info("scriptParams: "+scriptParams);
	log.debug("metadata: "+ctx.metadata);  3 + 4 + 2
}
''')
gscriptingService.run("foo.Bar")
gscriptingService.run("foo.Bar", [a:23, b:42])
```
In order to provide a script to your whole application, you can register a script under a qualified name. Register an updated script again with the same qualified name in order to reload it.

##Multi-threading and thread-safety
Running scripts is thread-safe. If a script is still running and you invoke run again, e.g. in another thread, a new instance will be created and started. The actual instance index can be accessed via the context (see above `instanceIndex`).
