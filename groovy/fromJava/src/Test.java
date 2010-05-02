
import java.io.*;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;

public class Test {

	public static void main(String[] args) throws Exception {

		GroovyClassLoader loader = new GroovyClassLoader();

		try {
			Class<Container> cl = loader.parseClass(new File("test.groovy"));

			Container tc = cl.newInstance();

			System.out.println("instance : " + tc);
			System.out.println("value : " + tc.getComponent("test"));

			Data data = (Data)tc.getComponent("data");
			System.out.printf("%s, %d \n", data.getName(), data.getPoint());

		} catch (CompilationFailedException ex) {
			ex.printStackTrace();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}