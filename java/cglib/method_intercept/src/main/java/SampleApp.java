
import net.sf.cglib.proxy.Enhancer;

public class SampleApp {
	public static void main(String... args) throws Throwable {
		Enhancer en = new Enhancer();

		en.setSuperclass(Proc.class);
		en.setCallback(new SampleInterceptor());

		Proc a = (Proc)en.create();
		int res = a.exec("test");

		System.out.println(res);
	}
}
