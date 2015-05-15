package sample;

import com.sun.tools.javac.tree.JCTree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DoExprBuilder {
	private static final String DO_TYPE = "$do";
	private Map<String, TemplateBuilder> builderMap = new HashMap<>();

	public DoExprBuilder() {
		builderMap.put("let",
				new TemplateBuilder("#{var}.bind(#{rExpr}, #{lExpr} -> #{body})", this::createBindParams));

		builderMap.put("return",
				new TemplateBuilder("#{var}.unit( #{expr} )", this::createBasicParams));

	}

	public Optional<String> build(JCLambda node) {
		return getDoVar(node).map(var -> createExpression((JCBlock)node.body, var));
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
