
import java.lang.invoke.SerializedLambda;
import java.util.function.Predicate;
import java.io.Serializable;

import org.benf.cfr.reader.util.getopt.GetOptParser;
import org.benf.cfr.reader.util.getopt.Options;
import org.benf.cfr.reader.util.getopt.OptionsImpl;
import org.benf.cfr.reader.entities.ClassFile;
import org.benf.cfr.reader.entities.Method;
import org.benf.cfr.reader.state.DCCommonState;
import org.benf.cfr.reader.util.output.ToStringDumper;

class Sample2 {
	public static void main(String... args) throws Exception {
		int a = 3;

		SPredicate<Integer> f = (x) -> x % a == 0;

		java.lang.reflect.Method m = f.getClass().getDeclaredMethod("writeReplace");
		m.setAccessible(true);

		SerializedLambda sl = (SerializedLambda)m.invoke(f);

		String src = decompileLambda(sl);

		System.out.println(src);
	}

	// ラムダ式の実装メソッドをデコンパイル
	private static String decompileLambda(SerializedLambda sl) throws Exception {
		ToStringDumper d = new ToStringDumper();

		Options options = new GetOptParser().parse(new String[] {sl.getImplClass()}, OptionsImpl.getFactory());
		DCCommonState dcCommonState = new DCCommonState(options);

		ClassFile c = dcCommonState.getClassFileMaybePath(options.getFileName());
		c = dcCommonState.getClassFile(c.getClassType());

		for (Method m : c.getMethodByName(sl.getImplMethodName())) {
			m.dump(d, true);
		}

		return d.toString();
	}

	public interface SPredicate<T> extends Predicate<T>, Serializable {
	}
}
