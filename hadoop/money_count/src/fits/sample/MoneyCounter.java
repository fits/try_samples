package fits.sample;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MoneyCounter {

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

		if (args.length < 2) {
			System.out.println("hadoop [input file] [output file]");
			return;
		}

		Job job = Job.getInstance(new Cluster(new Configuration()));
		job.setJobName("myjob");

		job.setMapperClass(SampleMapper.class);
		job.setReducerClass(SampleReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
	//	job.setOutputKeyClass(Text.class);
	//	job.setOutputValueClass(IntWritable.class);

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		boolean res = job.waitForCompletion(true);
		System.out.println("result = " + res);
	}

	static class SampleMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
		private final static IntWritable ONE = new IntWritable(1);

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			System.out.println("key: " + key + ", value: " + value);
			
			context.write(value, ONE);
		}
	}

	static class SampleReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

			int count = 0;
			for (IntWritable num : values) {
				count += num.get();
			}

			context.write(key, new IntWritable(Integer.parseInt(key.toString()) * count));
		}
	}
}