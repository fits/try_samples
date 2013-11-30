import com.caucho.quercus.script.QuercusScriptEngineFactory
import javax.script.SimpleScriptContext

def engine = new QuercusScriptEngineFactory().scriptEngine
def context = new SimpleScriptContext()

def sw = new StringWriter()
context.writer = sw

def res = engine.eval(new File(args[0]).getText('UTF-8'), context)

println "res = $res"
println "-----"
println sw
