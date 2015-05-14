package sample;

import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class DoExprVisitor extends TreeScanner {
	private static final String DO_TYPE = "$do";

	private ParserFactory parserFactory;
	private Map<String, TemplateBuilder> builderMap = new HashMap<>();
	private BiConsumer<JCLambda, JCExpression> changeNode = (lm, ne) -> {};

	public DoExprVisitor(Context context) {
		parserFactory = ParserFactory.instance(context);

		builderMap.put("let",
				new TemplateBuilder("#{var}.bind(#{rExpr}, #{lExpr} -> #{body})", this::createBindParams));

		builderMap.put("return",
				new TemplateBuilder("#{var}.unit( #{expr} )", this::createBasicParams));
	}

	@Override
	public void visitVarDef(JCVariableDecl node) {
		if (node.init != null) {
			changeNode = (lm, ne) -> {
				// 変数への代入式を置換
				if (node.init == lm) {
					node.init = ne;
				}
			};
		}
		super.visitVarDef(node);
	}

	@Override
	public void visitApply(JCMethodInvocation node) {
		if (node.args != null && node.args.size() > 0) {
			changeNode = (lm, ne) -> {
				// メソッドの引数を置換
				if (node.args.contains(lm)) {
					Stream<JCExpression> newArgs = node.args.stream().map(a -> (a == lm)? ne: a);
					node.args = com.sun.tools.javac.util.List.from(newArgs::iterator);
				}
			};
		}
		super.visitApply(node);
	}

	@Override
	public void visitLambda(JCLambda node) {
		getDoVar(node).ifPresent(var -> {
			// 変換後の処理内容を作成
			JCExpression ne = parseExpression(createExpression((JCBlock) node.body, var));
			fixPos(ne, node.pos);

			changeNode.accept(node, ne);
		});

		super.visitLambda(node);
	}

	// pos の値を修正する
	private void fixPos(JCExpression ne, final int basePos) {
		ne.accept(new TreeScanner() {
			@Override
			public void scan(JCTree tree) {
				if(tree != null) {
					tree.pos += basePos;
					super.scan(tree);
				}
			}
		});
	}

	private String createExpression(JCBlock block, String var) {
		String res = "";

		for (JCStatement st : block.stats.reverse()) {
			if (st instanceof JCReturn) {
				res = builderMap.get("return").build(var, res, ((JCReturn)st).expr.toString());
			}
			else if (st instanceof JCVariableDecl) {
				JCVariableDecl vd = (JCVariableDecl)st;

				if ("let".equals(vd.vartype.toString())) {
					Map<String, String> params = createBasicParams(var, res, "");
					params.put("lExpr", vd.name.toString());
					params.put("rExpr", vd.init.toString());

					if (vd.init instanceof JCLambda) {
						JCLambda lm = (JCLambda)vd.init;

						getDoVar(lm).ifPresent(childVar ->
								params.put("rExpr", createExpression((JCBlock) lm.body, childVar)));
					}
					res = builderMap.get("let").build(params);
				}
			}
		}
		return res;
	}

	private JCExpression parseExpression(String doExpr) {
		return parserFactory.newParser(doExpr, false, false, false).parseExpression();
	}

	private Optional<String> getDoVar(JCLambda node) {
		if (node.params.size() == 1) {
			String name = node.params.get(0).name.toString();

			if (name.endsWith(DO_TYPE)) {
				return Optional.of(name.replace(DO_TYPE, ""));
			}
		}
		return Optional.empty();
	}

	private Map<String, String> createBindParams(String var, String body, String expr) {
		Map<String, String> params = createBasicParams(var, body, expr);

		int eqPos = expr.indexOf("=");

		if (eqPos > 0) {
			params.put("lExpr", expr.substring(0, eqPos));
			params.put("rExpr", expr.substring(eqPos + 1));
		}

		return params;
	}

	private Map<String, String> createBasicParams(String var, String body, String expr) {
		Map<String, String> params = new HashMap<>();

		params.put("var", var);
		params.put("body", body);
		params.put("expr", expr);

		return params;
	}

	private interface ParamCreator {
		Map<String, String> create(String var, String body, String expr);
	}

	private class TemplateBuilder {
		private static final String VAR_PREFIX = "#{";
		private static final String VAR_SUFFIX = "}";

		private String template;
		private ParamCreator paramCreator;

		TemplateBuilder(String template, ParamCreator paramCreator) {
			this.template = template;
			this.paramCreator = paramCreator;
		}

		public String build(String var, String body, String expr) {
			return buildTemplate(template, paramCreator.create(var, body, expr));
		}

		public String build(Map<String, String> params) {
			return buildTemplate(template, params);
		}

		private String buildTemplate(String template, Map<String, String> params) {
			return params.entrySet().stream().reduce(template,
					(acc, v) -> acc.replace(VAR_PREFIX + v.getKey() + VAR_SUFFIX, v.getValue()),
					(a, b) -> a
			);
		}
	}
}
