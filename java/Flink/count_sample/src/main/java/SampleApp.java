import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class SampleApp {
    public static void main(String... args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        DataSet<String> input = env.fromElements(
            "1 10 5 50 100 1 10", 
            "100 1 1 10 5 100"
        );

        DataSet<Tuple2<String, Integer>> res = input
                .flatMap(new SampleSplitter())
                .groupBy(0)
                .sum(1);

        res.print();
    }

    private static class SampleSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> output) throws Exception {
            for (String s : value.split(" ")) {
                output.collect(new Tuple2<>(s, 1));
            }
        }
    }
}
