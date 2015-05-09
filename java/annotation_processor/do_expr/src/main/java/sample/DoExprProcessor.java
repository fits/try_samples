package sample;

import java.util.*;
import java.util.stream.Stream;
import javax.annotation.processing.*;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LambdaExpressionTree;

import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;

import com.sun.tools.javac.tree.JCTree.*;

import com.sun.tools.javac.util.Context;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class DoExprProcessor extends AbstractProcessor {
	private Trees trees;
	private Context context;

	@Override
	public void init(ProcessingEnvironment procEnv) {
		trees = Trees.instance(procEnv);
		
		JavacProcessingEnvironment env = (JavacProcessingEnvironment)procEnv;
		context = env.getContext();
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
		private static final String BIND_CODE = " return ${var}.bind(${rExpr}, new java.util.function.Function<${vType}, ${mType}<${vType}>>(){" +
												"  @Override public ${mType}<${vType}> apply(${vType} ${lExpr}){ ${body} }" +
												" });";
		private static final String UNIT_CODE = " ${body} return ${var}.unit( ${expr} );";

		private ParserFactory parserFactory;

		DoExprVisitor() {
			parserFactory = ParserFactory.instance(context);
		}

		@Override
		public Void visitLambdaExpression(LambdaExpressionTree node, Void p) {
			if (node instanceof JCLambda) {
				JCLambda lm = (JCLambda)node;

				if (isSingleTypeApplyParam(lm)) {

					JCVariableDecl param = lm.params.get(0);
					JCTypeApply paramType = (JCTypeApply)param.vartype;

					if (isDoType(paramType)) {
						lm.params = com.sun.tools.javac.util.List.nil();
						lm.paramKind = com.sun.tools.javac.tree.JCTree.JCLambda.ParameterKind.EXPLICIT;

						String mType = paramType.arguments.get(0).toString();
						String vType = paramType.arguments.get(1).toString();

						JCBlock block = (JCBlock)lm.body;

						block.stats = com.sun.tools.javac.util.List.of(
								convertToDoExpr(param.name.toString(), mType, vType, block));
					}
				}
			}
			return super.visitLambdaExpression(node, p);
		}

		private boolean isDoType(JCTypeApply paramType) {
			return DO_TYPE.equals(paramType.clazz.toString());
		}

		private boolean isSingleTypeApplyParam(JCLambda lm) {
			return lm.params.size() == 1
					&& lm.params.get(0).vartype instanceof JCTypeApply;
		}

		private JCStatement convertToDoExpr(String varName, String mType, String vType, JCBlock block) {
			Stream<String> revExpr = block.stats.reverse().stream().map(s -> s.toString().replaceAll(";", ""));

			HashMap<String, String> baseMap = new HashMap<>();
			baseMap.put("var", varName);
			baseMap.put("mType", mType);
			baseMap.put("vType", vType);

			String exprString = revExpr.reduce("", (acc, v) -> {
				int spacePos = v.indexOf(" ");

				String action = v.substring(0, spacePos);
				String expr = v.substring(spacePos + 1);

				switch (action) {
					case "let":
						acc = template(BIND_CODE, createBindParams(baseMap, acc, expr));
						break;
					case "return":
						acc = template(UNIT_CODE, createUnitParams(baseMap, acc, expr));
						break;
				}
				return acc;
			});

			return createExpression(exprString);
		}

		private HashMap<String, String> createBindParams(HashMap<String, String> baseMap, String body, String expr) {
			HashMap<String, String> params = createUnitParams(baseMap, body, expr);

			String[] vexp = expr.split("=");
			params.put("lExpr", vexp[0]);
			params.put("rExpr", vexp[1]);

			return params;
		}

		private HashMap<String, String> createUnitParams(HashMap<String, String> baseMap, String body, String expr) {
			HashMap<String, String> params = new HashMap<>(baseMap);
			params.put("body", body);
			params.put("expr", expr);
			return params;
		}

		private JCStatement createExpression(String doExpr) {
			JavacParser parser = parserFactory.newParser(doExpr, false, false, false);
			return parser.parseStatement();
		}

		private String template(String template, Map<String, String> params) {
			return params.entrySet().stream().reduce(template,
					(acc, v) -> acc.replaceAll("\\$\\{" + v.getKey() + "\\}", v.getValue()),
					(a, b) -> a);

		}
	}
}
