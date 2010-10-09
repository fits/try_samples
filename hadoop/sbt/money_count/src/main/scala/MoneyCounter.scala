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
			println("hadoop jar moneycounter.jar [input file] [output file]")
			return
		}

		val job = Job.getInstance(new Cluster(new Configuration))
		job.setJobName("myjob")

		job.setMapperClass(classOf[SampleMapper])
		job.setReducerClass(classOf[SampleReducer])

		job.setMapOutputKeyClass(classOf[Text])
		job.setMapOutputValueClass(classOf[IntWritable])

		FileInputFormat.setInputPaths(job, new Path(args(0)))
		FileOutputFormat.setOutputPath(job, new Path(args(1)))

		val res = job.waitForCompletion(true)
		println("result = " + res)
	}
}

class SampleMapper extends Mapper[LongWritable, Text, Text, IntWritable] {
	val one = new IntWritable(1)

	@throws(classOf[IOException])
	@throws(classOf[InterruptedException])
	def map(key: LongWritable, value: Text, context: Context) {
		context.write(value, one)
	}
}

class SampleReducer extends Reducer[Text, IntWritable, Text, IntWritable] {

	@throws(classOf[IOException])
	@throws(classOf[InterruptedException])
	def reduce(key: Text, values: Iterable[IntWritable], context: Context) {
		val count = values.foldLeft(0)(_ + _.get())
		context.write(key, new IntWritable(key.toString.toInt * count))
	}
}
