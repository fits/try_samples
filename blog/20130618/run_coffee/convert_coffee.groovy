import javax.script.*

def engine = new ScriptEngineManager().getEngineByExtension('js')

engine.eval(new FileReader('coffee-script.js'))

engine.put('coffeeSrc', new File(args[0]).getText('UTF-8'))

println engine.eval('CoffeeScript.compile(coffeeSrc)')
