package fits.sample

import scala.collection.JavaConversions._

import java.io.IOException
import org.apache.hadoop.conf._
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io._
import org.apache.hadoop.mapreduce._
import org.apache.hadoop.mapreduce.lib.input._
import org.apache.hadoop.mapreduce.lib.output._

object MoneyCounter {
	def main(args: Array[String]) {
		if (args.length < 2) {
			println("hadoop jar moneycounter.jar [input file] [output dir]")
			return
		}

		val job = Job.getInstance(new Cluster(new Configuration))
		job.setJobName("myjob")

		//分散実行モードで JAR ファイルをリモート側に手動で配置しないための設定
		//classOf[MoneyCounter] とできなかったので CountMapper を使用
		job.setJarByClass(classOf[SampleMapper])

		job.setMapperClass(classOf[SampleMapper])
		job.setReducerClass(classOf[SampleReducer])

		job.setMapOutputKeyClass(classOf[Text])
		job.setMapOutputValueClass(classOf[IntWritable])

		FileInputFormat.setInputPaths(job, new Path(args(0)))
		FileOutputFormat.setOutputPath(job, new Path(args(1)))

		val res = job.waitForCompletion(true)
		println("result = " + res)
	}

	//ジェネリックタイプを指定したMapperの別名定義
	type M = Mapper[LongWritable, Text, Text, IntWritable]

	//Mapper のサブクラス
	class SampleMapper extends M {
		val one = new IntWritable(1)

		//Context はジェネリックタイプを指定したMapperの
		//Contextを指定する必要あり
		override def map(key: LongWritable, value: Text, context: M#Context) {
			context.write(value, one)
		}
	}

	//ジェネリック型を指定したReducerの別名定義
	type R = Reducer[Text, IntWritable, Text, IntWritable]

	//Reducer のサブクラス
	class SampleReducer extends R {
		//Context はジェネリックタイプを指定したReducerの
		//Contextを指定する必要あり
		override def reduce(key: Text, values: java.lang.Iterable[IntWritable], context: R#Context) {

			val count = values.foldLeft(0)(_ + _.get())
			context.write(key, new IntWritable(key.toString.toInt * count))
		}
	}

}

