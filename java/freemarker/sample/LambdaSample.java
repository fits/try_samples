
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.StringWriter;
import java.util.function.BiFunction;

class LambdaSample {
	public static void main(String... args) throws Exception {
		Configuration conf = new Configuration(Configuration.VERSION_2_3_21);

		conf.setDirectoryForTemplateLoading(new File("./template"));
		conf.setDefaultEncoding("UTF-8");

		Template template = conf.getTemplate(args[0]);

		StringWriter sw = new StringWriter();

		BiFunction<Integer, Integer, Integer> f = Math::addExact;

		template.process(f, sw);

		System.out.println(sw.toString());
	}
}
