
import java.io.IOException;

import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;

import com.sun.source.util.JavacTask;

public class JavacTaskParse {
	public static void main(String... args) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		try (
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)
		) {
			JavacTask task = (JavacTask)compiler.getTask(null, fileManager, null, null, null, fileManager.getJavaFileObjects(args));

			task.parse().forEach(cu -> cu.accept(new SampleVisitor(), null));
		}
	}
}
