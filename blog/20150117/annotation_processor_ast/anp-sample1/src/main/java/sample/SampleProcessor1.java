package sample;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.annotation.processing.SupportedAnnotationTypes;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.sun.source.tree.CompilationUnitTree;

import com.sun.source.util.Trees;
import com.sun.source.util.TreePath;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class SampleProcessor1 extends AbstractProcessor {
	private Trees trees;

	@Override
	public void init(ProcessingEnvironment procEnv) {
		trees = Trees.instance(procEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		roundEnv.getRootElements().stream().map(this::toUnit).forEach(u -> {
			System.out.println("----- CompilationUnitTree -----");
			System.out.println(u);
		});

		return false;
	}

	private CompilationUnitTree toUnit(Element el) {
		TreePath path = trees.getPath(el);
		return path.getCompilationUnit();
	}
}
