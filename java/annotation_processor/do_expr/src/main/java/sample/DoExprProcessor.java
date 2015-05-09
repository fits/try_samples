package sample;

import java.util.Set;
import javax.annotation.processing.*;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Context;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class DoExprProcessor extends AbstractProcessor {
	private Trees trees;
	private Context context;

	@Override
	public void init(ProcessingEnvironment procEnv) {
		trees = Trees.instance(procEnv);
		context = ((JavacProcessingEnvironment)procEnv).getContext();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		roundEnv.getRootElements().stream().map(this::toUnit).forEach(this::processUnit);
		return false;
	}

	private CompilationUnitTree toUnit(Element el) {
		TreePath path = trees.getPath(el);
		return path.getCompilationUnit();
	}

	private void processUnit(CompilationUnitTree cu) {
		// AST 変換
		cu.accept(new DoExprVisitor(context), null);
		// 変換内容を出力
		System.out.println(cu);
	}
}
