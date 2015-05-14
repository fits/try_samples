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
		if (node.params.size() == 1) {
			getDoVar(node.params.get(0)).ifPresent(var -> {
				// 変換後の処理内容を作成
				JCExpression ne = parseExpression(createExpression((JCBlock) node.body, var));
				fixPos(ne, node.pos);

				changeNode.accept(node, ne);
			});
		}
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
		Stream<String> revExpr = block.stats.reverse().stream().map(s -> s.toString().replaceAll(";", ""));

		return revExpr.reduce("", (acc, v) -> {
			int spacePos = v.indexOf(" ");
			String action = v.substring(0, spacePos);

			if (builderMap.containsKey(action)) {
				acc = builderMap.get(action).build(var, acc, v.substring(spacePos + 1));
			}

			return acc;
		});
	}

	private JCExpression parseExpression(String doExpr) {
		return parserFactory.newParser(doExpr, false, false, false).parseExpression();
	}

	private Optional<String> getDoVar(JCVariableDecl param) {
		String name = param.name.toString();

		return name.endsWith(DO_TYPE)? Optional.of(name.replace(DO_TYPE, "")): Optional.empty();
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

		private String buildTemplate(String template, Map<String, String> params) {
			return params.entrySet().stream().reduce(template,
					(acc, v) -> acc.replace(VAR_PREFIX + v.getKey() + VAR_SUFFIX, v.getValue()),
					(a, b) -> a
			);
		}
	}
}
