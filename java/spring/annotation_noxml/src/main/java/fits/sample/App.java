package fits.sample;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;


public class App {

	public static void main( String[] args ) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

		PropertyOverrideConfigurer prop = new PropertyOverrideConfigurer();
		prop.setLocation(ctx.getResource("classpath:fits/sample/sample.properties"));
		ctx.addBeanFactoryPostProcessor(prop);

		ctx.scan("fits.sample");
		ctx.refresh();

		SampleWrapper w = ctx.getBean("sampleWrapper", SampleWrapper.class);
		w.test();
	}
}
