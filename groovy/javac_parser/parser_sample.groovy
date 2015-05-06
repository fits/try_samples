
import javax.tools.JavaFileManager
import javax.tools.ToolProvider

import com.sun.tools.javac.parser.ParserFactory
import com.sun.tools.javac.util.Context

def ctx = new Context()
ctx.put(JavaFileManager, ToolProvider.systemJavaCompiler.getStandardFileManager(null, null, null))

def factory = ParserFactory.instance(ctx)

def str = 'opt.bind("a", (x)->{ return opt.bind(); })'

def parser = factory.newParser(str, false, false, false)

println parser

def exp = parser.parseExpression()

println exp.class
println exp
