
import com.sun.source.util.TreeScanner;
import com.sun.source.tree.*;

public class SampleVisitor extends TreeScanner<Void, Void> {
	@Override
	public Void visitClass(ClassTree node, Void p) {
		System.out.println("class: " + node.getSimpleName());
		return super.visitClass(node, p);
	}

	@Override
	public Void visitMethod(MethodTree node, Void p) {
		System.out.println("method: " + node.getName());
		return super.visitMethod(node, p);
	}

	@Override
	public Void visitLambdaExpression(LambdaExpressionTree node, Void p) {
		System.out.println("lambda: " + node);
		return super.visitLambdaExpression(node, p);
	}
}