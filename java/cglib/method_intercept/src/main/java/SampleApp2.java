
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class SampleApp2 {
	public static void main(String... args) throws Throwable {

		Proc a = enhance(Proc.class, new SampleInterceptor());

		int res = a.exec("test");

		System.out.println(res);
	}

	@SuppressWarnings("unchecked")
	private static <T> T enhance(Class<T> cls, MethodInterceptor mi) throws Throwable {
		Enhancer en = new Enhancer();

		en.setSuperclass(cls);
		en.setCallback(mi);

		return (T)en.create();
	}
}
