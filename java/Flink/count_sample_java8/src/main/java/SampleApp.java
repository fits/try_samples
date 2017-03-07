
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.util.Arrays;

public class SampleApp {
	public static void main(String... args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.createLocalEnvironment();

        DataSet<String> input = env.fromElements(
                "1 10 5 50 100 1 10",
                "100 1 1 10 5 100"
        );

        DataSet<Tuple2<String, Integer>> res = input
                .flatMap((String value, Collector<Tuple2<String, Integer>> output) ->
                    Arrays.stream(value.split("\\s"))
                            .forEach(w -> output.collect(new Tuple2<>(w, 1)))
                )
                .groupBy(0)
                .sum(1);

	    res.print();
	}
}