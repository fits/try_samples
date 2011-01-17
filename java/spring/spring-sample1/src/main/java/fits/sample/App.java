package fits.sample;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	public static void main( String[] args ) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		SampleWrapper w = (SampleWrapper)ctx.getBean("wrapper");
		w.test();
	}
}
