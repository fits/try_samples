package sample;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;
import javax.annotation.processing.*;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.tools.javac.model.JavacElements;

import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;

import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree.*;

import com.sun.tools.javac.util.Context;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class DoExprProcessor extends AbstractProcessor {
	private Trees trees;
	private Context context;
	private TreeMaker maker;
	private JavacElements elements;

	@Override
	public void init(ProcessingEnvironment procEnv) {
		trees = Trees.instance(procEnv);
		
		JavacProcessingEnvironment env = (JavacProcessingEnvironment)procEnv;
		context = env.getContext();
		
		maker = TreeMaker.instance(context);
		elements = JavacElements.instance(context);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		roundEnv.getRootElements().stream().map(this::toUnit).forEach(this::processUnit);

		return false;
	}

	private CompilationUnitTree toUnit(Element el) {
		TreePath path = trees.getPath(el);
		return path.getCompilationUnit();
	}

	private void processUnit(CompilationUnitTree cu) {
		cu.accept(new DoExprVisitor(), null);
		System.out.println(cu);
	}

	private class DoExprVisitor extends TreeScanner<Void, Void> {
		public static final String DO_TYPE = "$do";
		private ParserFactory parserFactory;

		DoExprVisitor() {
			parserFactory = ParserFactory.instance(context);
		}

		@Override
		public Void visitLambdaExpression(LambdaExpressionTree node, Void p) {
			if (node instanceof JCLambda) {
				JCLambda lm = (JCLambda)node;

				if (lm.params.size() == 1
						&& lm.params.get(0).vartype instanceof JCTypeApply) {

					JCVariableDecl param = lm.params.get(0);
					JCTypeApply paramType = (JCTypeApply)param.vartype;

					if (DO_TYPE.equals(paramType.clazz.toString())) {
						String varName = param.name.toString();
						String mType = paramType.arguments.get(0).toString();
						String vType = paramType.arguments.get(1).toString();

						lm.params = com.sun.tools.javac.util.List.nil();
						lm.paramKind = com.sun.tools.javac.tree.JCTree.JCLambda.ParameterKind.EXPLICIT;

						JCBlock block = (JCBlock)lm.body;

						block.stats = com.sun.tools.javac.util.List.of(
								convertToDoExpr(varName, mType, vType, block));
					}
				}
			}
			return super.visitLambdaExpression(node, p);
		}

		private JCStatement convertToDoExpr(String varName, String mType, String vType, JCBlock block) {
			Stream<String> expr = block.stats.stream().map(s -> s.toString().replaceAll(";", ""));

			String resType = mType + "<" + vType + ">";

			BinaryOperator<String> bindType = (arg, proc) -> "new java.util.function.Function<" + vType + ", " + resType + ">(){ @Override public " + resType + " apply(" + vType + " " + arg + ") { " + proc + " }}";

			String exprString = reverse(expr).reduce("", (acc, v) -> {
				if (v.startsWith("let")) {
					String[] vexp = v.substring(3).split("=");

					acc = " return " + varName + ".bind(" + vexp[1].trim() + ", " + bindType.apply(vexp[0].trim(), acc) + ");";
				}
				else if (v.startsWith("return")) {
					acc = acc + " return " + varName + ".unit(" + v.substring(6).trim() + ");";
				}
				return acc;
			});

			return createExpression(exprString);
		}

		private JCStatement createExpression(String doExpr) {
			JavacParser parser = parserFactory.newParser(doExpr, false, false, false);
			return parser.parseStatement();
		}

		private <T> Stream<T> reverse(Stream<T> src) {
			return src.sorted((a, b) -> -1);
		}
	}
}
