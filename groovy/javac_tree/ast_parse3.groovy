
import javax.tools.ToolProvider

import com.sun.tools.javac.main.JavaCompiler
import com.sun.tools.javac.util.Context

import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeScanner

class SampleVisitor extends TreeScanner {
	@Override
	void visitVarDef(JCTree.JCVariableDecl node) {
		println '----- variable -----'

		println node.dump()

		super.visitVarDef(node)

	}

	@Override
	void visitLambda(JCTree.JCLambda node) {
		println '----- lambda -----'

		println node.dump()

		super.visitLambda(node)
	}
}

def fileManager = ToolProvider.systemJavaCompiler.getStandardFileManager(null, null, null)

def compiler = new JavaCompiler(new Context())

fileManager.getJavaFileObjects(args).each {
	def cu = compiler.parse(it)

	println cu

	cu.accept(new SampleVisitor())
}
