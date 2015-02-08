
import java.io.IOException;

import javax.tools.ToolProvider;
import javax.tools.StandardJavaFileManager;

import com.sun.source.tree.CompilationUnitTree;

import com.sun.tools.javac.main.JavaCompiler;
import com.sun.tools.javac.util.Context;

public class JavaCompilerParse {
	public static void main(String... args) throws IOException {
		try (
			StandardJavaFileManager fileManager = 
				ToolProvider.getSystemJavaCompiler()
					.getStandardFileManager(null, null, null)
		) {
			JavaCompiler compiler = new JavaCompiler(new Context());

			fileManager.getJavaFileObjects(args).forEach(f -> {
				// JCCompilationUnit
				CompilationUnitTree cu = compiler.parse(f);

				cu.accept(new SampleVisitor(), null);
			});
		}
	}
}
