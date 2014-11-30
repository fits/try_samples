
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.StringWriter;

class Sample {
	public static void main(String... args) throws Exception {
		Configuration conf = new Configuration(Configuration.VERSION_2_3_21);

		conf.setDirectoryForTemplateLoading(new File("./template"));
		conf.setDefaultEncoding("UTF-8");

		Template template = conf.getTemplate(args[0]);

		StringWriter sw = new StringWriter();

		template.process(null, sw);

		System.out.println(sw.toString());
	}
}
