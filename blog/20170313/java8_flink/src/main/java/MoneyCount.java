
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;

public class MoneyCount {
    public static void main(String... args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.createLocalEnvironment();

        env.readTextFile(args[0])
            .map( w -> new Tuple2<>(w, 1) )
            .groupBy(0)
            .sum(1)
            .print();
    }
}