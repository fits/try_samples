
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

import java.util.function.DoubleConsumer;

public class SampleApp {
	public static void main(String... args) {
		float[] r1 = calc(
			new float[] {1f, 2f, 3f, 4f, 5f}, 
			new float[] {10f, 20f, 30f, 40f, 50f}
		);

		printResult(r1, System.out::println);
	}

	private static float[] calc(final float[] a, final float[] b) {
		int len = Math.min(a.length, b.length);

		final float[] res = new float[len];

		Kernel kernel = new Kernel() {
			public void run() {
				int id = getGlobalId();
				res[id] = a[id] * b[id] + 1;
			}
		};

		kernel.execute(Range.create(len));

		printExecutionMode("calc", kernel);

		return res;
	}

	private static void printExecutionMode(String name, Kernel kernel) {
		System.out.printf(
			"*** %s, mode=%s, isOpenCL=%b", 
			name, 
			kernel.getExecutionMode().name(), 
			kernel.getExecutionMode().isOpenCL()
		);

		System.out.println();
	}

	private static void printResult(float[] res, DoubleConsumer consumer) {
		for (int i = 0; i < res.length; i++) {
			consumer.accept(res[i]);
		}
	}
}
