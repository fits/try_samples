package sample;

import com.sun.tools.javac.tree.JCTree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DoExprBuilder {
	private static final String DO_TYPE = "$do";
	private static final String VAR_PREFIX = "#{";
	private static final String VAR_SUFFIX = "}";
	// let 用のコードテンプレート
	private static final String LET_CODE = "#{var}.bind(#{rExpr}, #{lExpr} -> #{body})";
	// return 用のコードテンプレート
	private static final String RETURN_CODE = "#{var}.unit( #{expr} )";

	private Map<Class<? extends JCStatement>, CodeGenerator<JCStatement>> builderMap = new HashMap<>();

	public DoExprBuilder() {
		builderMap.put(JCVariableDecl.class, (n, v, b) -> generateCodeForLet(cast(n), v, b));
		builderMap.put(JCReturn.class, (n, v, b) -> generateCodeForReturn(cast(n), v, b));
	}

	public Optional<String> build(JCLambda node) {
		return getDoVar(node).map(var -> createExpression((JCBlock)node.body, var));
	}

	private String createExpression(JCBlock block, String var) {
		String res = "";

		for (JCStatement st : block.stats.reverse()) {
			res = builderMap.getOrDefault(st.getClass(), this::generateNoneCode).generate(st, var, res);
		}
		return res;
	}

	private String generateNoneCode(JCStatement node, String var, String body) {
		return body;
	}

	// let 用のソースコード生成
	private String generateCodeForLet(JCVariableDecl node, String var, String body) {
		String res = body;

		if ("let".equals(node.vartype.toString())) {
			Map<String, String> params = createParams(var);
			params.put("body", res);
			params.put("lExpr", node.name.toString());
			params.put("rExpr", node.init.toString());

			// 入れ子への対応
			if (node.init instanceof JCLambda) {
				JCLambda lm = cast(node.init);

				getDoVar(lm).ifPresent(childVar ->
						params.put("rExpr", createExpression((JCBlock) lm.body, childVar)));
			}
			res = buildTemplate(LET_CODE, params);
		}

		return res;
	}

	// return 用のソースコード生成
	private String generateCodeForReturn(JCReturn node, String var, String body) {
		Map<String, String> params = createParams(var);
		params.put("expr", node.expr.toString());

		return buildTemplate(RETURN_CODE, params);
	}

	// 処理変数名の抽出
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

	// テンプレート処理
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

	private interface CodeGenerator<T> {
		String generate(T node, String var, String body);
	}
}
