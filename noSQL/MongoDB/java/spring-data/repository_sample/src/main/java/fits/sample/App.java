package fits.sample;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

//アプリケーションクラス
public class App {

	public static void main( String[] args ) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");

		SampleService ss = ctx.getBean(SampleService.class);

		ss.addData(Arrays.asList(
			new Data("test1", 10),
			new Data("sample", 100),
			new Data("spring-test-10", 50),
			new Data("test-2", 20)
		));

		List<Data> list = ss.getData("sample");
		assert list.get(0).getPoint() == 100;

		for (Data d : ss.findData("test", 15)) {
			System.out.printf("id: %d, name: %s, point: %d\n", d.getId(), d.getName(), d.getPoint());
		}
	}
}
