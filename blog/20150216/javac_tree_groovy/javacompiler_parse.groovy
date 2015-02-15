
import javax.tools.ToolProvider

import com.sun.tools.javac.main.JavaCompiler
import com.sun.tools.javac.util.Context

def fileManager = ToolProvider.systemJavaCompiler.getStandardFileManager(null, null, null)

def compiler = new JavaCompiler(new Context())

fileManager.getJavaFileObjects(args).each {
	def cu = compiler.parse(it)
	cu.accept(new SampleVisitor(), null)
}
