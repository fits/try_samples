package fits.sample;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;


public class App {

	public static void main( String[] args ) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

		PropertyOverrideConfigurer prop = new PropertyOverrideConfigurer();
		prop.setLocation(ctx.getResource("classpath:fits/sample/mongodb.properties"));
		ctx.addBeanFactoryPostProcessor(prop);

		ctx.scan("fits.sample");
		ctx.refresh();

		DbOperations dbo = ctx.getBean("mongoDbOperations", DbOperations.class);
		dbo.put("no1", new Data("test1", 5));

		Data d = dbo.get("no1", Data.class);

		System.out.printf("data = %s, %d \n", d.getName(), d.getPoint());
	}
}
