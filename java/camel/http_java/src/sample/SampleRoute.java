
package sample;

import java.io.FileInputStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileExchange;

public class SampleRoute extends RouteBuilder {

	public void configure() {

		from("jetty:http://localhost/test").to("direct:response");

		from("direct:response").process(new Processor() {
			public void process(Exchange exchange) {

				HttpServletRequest req = (HttpServletRequest)exchange.getIn().getBody(javax.servlet.http.HttpServletRequest.class);

				String id = req.getParameter("id");

				exchange.getOut().setBody("<html><body><h1>id=" + id + "</h1></body></html>");
			}
		}).to("file:logs");

		from("file:logs").process(new Processor() {
			public void process(Exchange exchange) throws Exception {
				FileExchange fe = (FileExchange)exchange;

				//ファイル名とファイル内容を出力
				System.out.print(fe.getFile().getPath() + " : ");

				FileInputStream fis = new FileInputStream(fe.getFile());

				try {
					FileChannel fc = fis.getChannel();
					MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
					CharBuffer chb = Charset.defaultCharset().decode(buffer);

					System.out.println(chb.toString());
				}
				finally {
					fis.close();
				}
			}
		});
	}

	public static void main(String[] args) throws Exception {

		DefaultCamelContext ctx = new DefaultCamelContext();

		ctx.addRoutes(new SampleRoute());

		ctx.start();

		System.out.println("start Camel");

		System.in.read();

		ctx.stop();
	}
}

