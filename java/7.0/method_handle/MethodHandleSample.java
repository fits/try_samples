
import java.lang.invoke.MethodHandle;
import java.io.PrintStream;

import static java.lang.invoke.MethodHandles.*;
import static java.lang.invoke.MethodType.*;

public class MethodHandleSample {
	public static void main(String... args) throws Throwable {

		MethodHandle mh = publicLookup().findVirtual(PrintStream.class, "printf", methodType(PrintStream.class, String.class, Object[].class));

		System.out.println(mh);

	//	MethodHandle mh2 = insertArguments(mh, 0, "%s, %d");

		PrintStream ps1 = (PrintStream)mh.invokeExact(System.out, "%s, %d\n", new Object[]{ "sample1-1", 1 });

		PrintStream ps2 = (PrintStream)mh.invokeWithArguments(System.out, "%s, %d\n", "sample1-2", 2);

		// 下記は WrongMethodTypeException が発生
		// PrintStream ps3 = (PrintStream)mh.invokeExact(System.out, "%s, %d", "sample1-3", 3);

		// 下記は戻り値が void 扱いになり WrongMethodTypeException が発生
		//mh.invokeExact(System.out, "%s, %d", new Object[]{ "sample", 3 });

		System.out.println("---------------");

		PrintStream bps1 = (PrintStream)mh.bindTo(System.out).bindTo("%s, %d\n").invokeExact(new Object[] { "sample2-1", 4 });

		// 下記は WrongMethodTypeException が発生
		//PrintStream bps2 = (PrintStream)mh.bindTo(System.out).bindTo("%s, %d\n").invokeWithArguments("sample2-2", 5);
	}
}