package sample;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class SampleProcessor extends AbstractProcessor {
	@Override
	public void init(ProcessingEnvironment procEnv) {
		super.init(procEnv);

	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		System.out.println("*** call SampleProcessor.process() : " + roundEnv);

		annotations.forEach(te -> {
			System.out.printf("TypeElement simpleName=%s, qualifiedName=%s\n",
					te.getSimpleName(), te.getQualifiedName());

			System.out.println("annotatedwith:");
			roundEnv.getElementsAnnotatedWith(te).forEach(el -> System.out.printf("    %s\n", el));
		});

		System.out.println("root:");
		roundEnv.getRootElements().forEach(el -> System.out.printf("    %s\n", el));

		return false;
	}
}
