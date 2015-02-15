
import javax.tools.ToolProvider

def compiler = ToolProvider.systemJavaCompiler
def fileManager = compiler.getStandardFileManager(null, null, null)

def task = compiler.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjects(args))

task.parse().each {
	// MissingMethodException (No signature of method)
	it.accept(new SampleVisitor(), null)
}
