package sample;

import com.sun.tools.javac.tree.JCTree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DoExprBuilder {
	private static final String DO_TYPE = "$do";
	private static final String VAR_PREFIX = "#{";
	private static final String VAR_SUFFIX = "}";
	private static final String LET_CODE = "#{var}.bind(#{rExpr}, #{lExpr} -> #{body})";
	private static final String RETURN_CODE = "#{var}.unit( #{expr} )";

	private Map<Class<? extends JCStatement>, CodeCreator<JCStatement>> builderMap = new HashMap<>();

	public DoExprBuilder() {
		builderMap.put(JCVariableDecl.class, (n, v, b) -> createCodeForJCVariableDecl(cast(n), v, b));
		builderMap.put(JCReturn.class, (n, v, b) -> createCodeForJCReturn(cast(n), v, b));
	}

	public Optional<String> build(JCLambda node) {
		return getDoVar(node).map(var -> createExpression((JCBlock)node.body, var));
	}

	private String createExpression(JCBlock block, String var) {
		String res = "";

		for (JCStatement st : block.stats.reverse()) {
			res = builderMap.getOrDefault(st.getClass(), this::createCode).create(st, var, res);
		}
		return res;
	}

	private String createCode(JCStatement node, String var, String body) {
		return body;
	}

	private String createCodeForJCVariableDecl(JCVariableDecl node, String var, String body) {
		String res = body;

		if ("let".equals(node.vartype.toString())) {
			Map<String, String> params = createParams(var);
			params.put("body", res);
			params.put("lExpr", node.name.toString());
			params.put("rExpr", node.init.toString());

			if (node.init instanceof JCLambda) {
				JCLambda lm = (JCLambda)node.init;

				getDoVar(lm).ifPresent(childVar ->
						params.put("rExpr", createExpression((JCBlock) lm.body, childVar)));
			}
			res = buildTemplate(LET_CODE, params);
		}

		return res;
	}

	private String createCodeForJCReturn(JCReturn node, String var, String body) {
		Map<String, String> params = createParams(var);
		params.put("expr", node.expr.toString());

		return buildTemplate(RETURN_CODE, params);
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

	private Map<String, String> createParams(String var) {
		Map<String, String> params = new HashMap<>();

		params.put("var", var);

		return params;
	}

	private String buildTemplate(String template, Map<String, String> params) {
		return params.entrySet().stream().reduce(template,
				(acc, v) -> acc.replace(VAR_PREFIX + v.getKey() + VAR_SUFFIX, v.getValue()),
				(a, b) -> a
		);
	}

	@SuppressWarnings("unchecked")
	private <S, T> T cast(S obj) {
		return (T)obj;
	}

	private interface CodeCreator<T> {
		String create(T node, String var, String body);
	}
}
