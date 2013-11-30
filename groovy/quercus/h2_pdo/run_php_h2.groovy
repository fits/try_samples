@GrabResolver('http://caucho.com/m2')
@Grab('com.caucho:resin:4.0.30')
@Grab('com.h2database:h2:1.3.174')
import com.caucho.quercus.script.QuercusScriptEngineFactory
import javax.script.SimpleScriptContext

def engine = new QuercusScriptEngineFactory().scriptEngine
// JDBC ドライバークラスの設定が必要
engine.quercus.jdbcDriverContext.setProtocol('h2', 'org.h2.Driver')

def context = new SimpleScriptContext()

def sw = new StringWriter()
context.writer = sw

def res = engine.eval(new File(args[0]).getText('UTF-8'), context)

println sw
