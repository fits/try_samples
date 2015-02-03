
import java.io.IOException;

import javax.tools.ToolProvider;
import javax.tools.StandardJavaFileManager;

import com.sun.source.util.TreeScanner;
import com.sun.source.tree.*;

import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.util.Context;

public class ASTParse {
	public static void main(String... args) throws IOException {
		try (
			StandardJavaFileManager fileManager = 
				ToolProvider.getSystemJavaCompiler()
					.getStandardFileManager(null, null, null)) {

			JavaCompiler compiler = new JavaCompiler(new Context());

			fileManager.getJavaFileObjects(args).forEach(f -> {
				// JCCompilationUnit
				CompilationUnitTree cu = compiler.parse(f);

				cu.accept(new SampleVisitor(), null);
			});
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
