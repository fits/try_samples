
import javax.tools.JavaFileManager
import javax.tools.ToolProvider

import com.sun.tools.javac.parser.ParserFactory
import com.sun.tools.javac.util.Context

def ctx = new Context()
ctx.put(JavaFileManager, ToolProvider.systemJavaCompiler.getStandardFileManager(null, null, null))

def factory = ParserFactory.instance(ctx)

def parser = factory.newParser(new File(args[0]).text, false, false, false)

def cu = parser.parseCompilationUnit()

println cu
