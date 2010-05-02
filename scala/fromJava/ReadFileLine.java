import scala.Iterator;
import scala.io.Source$;

public class ReadFileLine {
	public static void main(String[] args) {

		//Singleton オブジェクトは  object名$.MODULE$ でアクセス可
		Iterator<String> it = Source$.MODULE$.fromFile(args[0]).getLines();

		while(it.hasNext()) {
			System.out.print(it.next());
		}
	}
}
