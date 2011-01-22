package fits.sample;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	public static void main( String[] args ) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext("fits.sample");

		SampleWrapper w = (SampleWrapper)ctx.getBean(SampleWrapper.class);
		w.test();
	}
}
