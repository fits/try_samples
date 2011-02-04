package fits.sample;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//アプリケーションクラス
public class App {

	public static void main( String[] args ) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		DbOperations dbo = ctx.getBean(DbOperations.class);

		dbo.put("no1", new Data("test1", 5));

		Data d = dbo.get("no1", Data.class);

		System.out.printf("data = %s, %d \n", d.getName(), d.getPoint());
	}
}
