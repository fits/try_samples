
import java.io.IOException;

import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import com.sun.source.tree.*;

public class ASTParse2 {
	public static void main(String... args) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		try (
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)
		) {
			JavacTask task = (JavacTask)compiler.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjects(args));

			task.parse().forEach(cu -> cu.accept(new SampleVisitor(), null));
		}
	}

	static class SampleVisitor extends TreeScanner<Void, Void> {
		@Override
		public Void visitCompilationUnit(CompilationUnitTree node, Void p)  {
			System.out.println("**** cunit: " + node);

			super.visitCompilationUnit(node, p);
			return null;
		}

		@Override
		public Void visitClass(ClassTree node, Void p) {
			System.out.println("*** class: " + node);

			super.visitClass(node, p);
			return null;
		}

		@Override
		public Void visitMethod(MethodTree node, Void p) {
			System.out.println("**** method: " + node);

			super.visitMethod(node, p);
			return null;
		}
	}
}
