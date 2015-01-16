package sample;

import java.util.*;
import javax.annotation.processing.*;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.model.JavacElements;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import com.sun.source.tree.VariableTree;

import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreeScanner;

import com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.JCTree.JCExpression;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class VarTypeChangeProcessor extends AbstractProcessor {
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
		cu.accept(new VarVisitor(), null);
	}

	private class VarVisitor extends TreeScanner<Void, Void> {
		@Override
		public Void visitVariable(VariableTree node, Void p) {
			System.out.println("visitVariable: " + node);

			if (node instanceof JCVariableDecl) {
				JCVariableDecl vd = (JCVariableDecl)node;

				if ("var".equals(vd.vartype.toString())) {
					JCExpression ex = maker.Ident(elements.getName("java"));
					ex = maker.Select(ex, elements.getName("lang"));
					ex = maker.Select(ex, elements.getName("Object"));

					vd.vartype = ex;
				}
			}
			return null;
		}
	}
}
