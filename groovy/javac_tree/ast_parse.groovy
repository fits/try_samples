
import javax.tools.ToolProvider
import com.sun.tools.javac.util.Context
import com.sun.tools.javac.main.JavaCompiler

import com.sun.source.util.TreeScanner
import com.sun.source.tree.*

if (args.length < 1) {
	println '<java source file> ÅEÅEÅE'
	return
}

def fileManager = ToolProvider.systemJavaCompiler.getStandardFileManager(null, null, null)

def compiler = new JavaCompiler(new Context())

class SampleVisitor extends TreeScanner<Void, Void> {
	Void visitCompilationUnit(CompilationUnitTree node, Void p)  {
		println "**** cunit: ${node}"

		super.visitCompilationUnit(node, p)
	}

	Void visitClass(ClassTree node, Void p) {
		println "*** class: ${node}"

		super.visitClass(node, p)
	}

	Void visitMethod(MethodTree node, Void p) {
		println "**** method: ${node}"

		super.visitMethod(node, p)
	}
}

fileManager.getJavaFileObjects(args).each {
	def cu = compiler.parse(it)

	cu.accept(new SampleVisitor(), null)
}

fileManager.close()
