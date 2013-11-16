package fits.sample;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Tuple2;

class MoneyCount {
	public static void main(String... args) {

		JavaSparkContext spark = new JavaSparkContext("local", "MoneyCount");

		JavaRDD<String> file = spark.textFile(args[0]);

		JavaPairRDD<String, Integer> res = file.map(new PairFunction<String, String, Integer>() {
			public Tuple2<String, Integer> call(String s) {
				return new Tuple2<String, Integer>(s, 1);
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			public Integer call(Integer t1, Integer t2) {
				return t1 + t2;
			}
		});

		res.foreach(new VoidFunction<Tuple2<String, Integer>>() {
			public void call(Tuple2<String, Integer> t) {
				System.out.println(t._1 + " = " + t._2);
			}
		});
	}
}
