import javax.script.*

def engine = new ScriptEngineManager().getEngineByExtension('js')

engine.eval(new FileReader('coffee-script.js'))

engine.put('coffeeSrc', new File(args[0]).getText('UTF-8'))

engine.eval('eval(CoffeeScript.compile(coffeeSrc))')

//def script = engine.eval('CoffeeScript.compile(coffeeSrc)')
//engine.eval(script, engine.createBindings())
