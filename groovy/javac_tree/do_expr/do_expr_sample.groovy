
import javax.tools.JavaFileManager
import javax.tools.ToolProvider

import com.sun.tools.javac.main.JavaCompiler
import com.sun.tools.javac.util.Context
import com.sun.tools.javac.parser.ParserFactory

import com.sun.source.util.TreeScanner
import com.sun.source.tree.*

class SampleVisitor extends TreeScanner<Void, Void> {
	Context context

	@Override
	Void visitLambdaExpression(LambdaExpressionTree node, Void p) {

		if (node.params.size() == 1) {
			def param = node.params.head().name.toString()

			if (param.endsWith('$do')) {
				def var = param.replace('$do', '')

				node.params = com.sun.tools.javac.util.List.nil()
				node.paramKind = com.sun.tools.javac.tree.JCTree.JCLambda.ParameterKind.EXPLICIT

				def expNodes = node.body.stats.collect { it.toString().replaceAll(';', '') }

				def doExp = createExpression(convertToComputation(var, expNodes))

				node.body.stats = com.sun.tools.javac.util.List.of(doExp)

				println node
			}
		}

		return super.visitLambdaExpression(node, p)
	}

	private convertToComputation(var, expNodes) {
		expNodes.reverse().inject('') { acc, v ->
			if (v.startsWith('let')) {
				def vexp = v.substring(3).split('=').collect { it.trim() }

				acc = "${var}.bind(${vexp.last()}, ${vexp.first()} -> ${acc} )"
			}
			else if (v.startsWith('return')) {
				acc = acc + "${var}.unit(${v.substring(6).trim()})"
			}
			acc
		}
	}

	private createExpression(String expr) {
		def factory = ParserFactory.instance(context)
		def parser = factory.newParser(expr, false, false, false)

		parser.parseExpression()
	}
}

def fileManager = ToolProvider.systemJavaCompiler.getStandardFileManager(null, null, null)

def ctx = new Context()
ctx.put(JavaFileManager, fileManager)

def compiler = new JavaCompiler(ctx)

fileManager.getJavaFileObjects(args).each {
	def cu = compiler.parse(it)

	cu.accept(new SampleVisitor(context: ctx), null)
}
