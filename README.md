grails-gscripting
=================

Run Groovy scripts in Grails

First Examples:

Logging (Config.groovy):
````
debug 'grails.plugin.gscripting',
      'grails.app.services.grails.plugin.gscripting'
````

Create script and run:
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

Register script and run by qualified name:
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
