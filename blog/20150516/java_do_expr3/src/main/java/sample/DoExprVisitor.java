package sample;

import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Context;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class DoExprVisitor extends TreeScanner {
	private ParserFactory parserFactory;
	private BiConsumer<JCLambda, JCExpression> changeNode = (lm, ne) -> {};
	private DoExprBuilder builder = new DoExprBuilder();

	public DoExprVisitor(Context context) {
		parserFactory = ParserFactory.instance(context);
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
		builder.build(node).ifPresent(expr -> {
			JCExpression ne = parseExpression(expr);
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

	private JCExpression parseExpression(String doExpr) {
		return parserFactory.newParser(doExpr, false, false, false).parseExpression();
	}
}
