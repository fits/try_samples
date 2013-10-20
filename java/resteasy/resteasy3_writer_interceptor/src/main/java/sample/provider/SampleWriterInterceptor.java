package sample.provider;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

@Provider
public class SampleWriterInterceptor implements WriterInterceptor {
	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		Object data = context.getEntity();

		System.out.println("write : " + data);

		if (data instanceof String && data != null) {
			Writer writer = new OutputStreamWriter(context.getOutputStream(), "UTF-8");
			writer.write((String)data);
			writer.flush();
		}
		else {
			context.proceed();
		}
	}
}
