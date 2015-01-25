package sample;

import java.util.*;
import javax.annotation.processing.*;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.tools.javac.model.JavacElements;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;

import com.sun.tools.javac.tree.TreeMaker;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class LambdaProcessor extends AbstractProcessor {
	private Trees trees;
	private TreeMaker maker;
	private JavacElements elements;

	@Override
	public void init(ProcessingEnvironment procEnv) {
		trees = Trees.instance(procEnv);
		
		JavacProcessingEnvironment env = (JavacProcessingEnvironment)procEnv;
		
		maker = TreeMaker.instance(env.getContext());
		elements = JavacElements.instance(env.getContext());
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
		cu.accept(new LambdaVisitor(), null);
	}

	private class LambdaVisitor extends TreeScanner<Void, Void> {
		@Override
		public Void visitLambdaExpression(LambdaExpressionTree node, Void p) {
			System.out.println("visitLambda: " + node + ", " + node.getClass());
			System.out.println("body: " + node.getBody() + ", params: " + node.getParameters());

			return null;
		}
	}
}
