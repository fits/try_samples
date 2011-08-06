package fits.sample.ws;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.core.util.ReaderWriter;

import fits.sample.model.Task;

@Provider
public class TaskWriter implements MessageBodyWriter<Task> {

	@Override
	public long getSize(Task value, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {

		return -1L;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

		return (Task.class == type);
	}

	@Override
	public void writeTo(Task value, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> params, OutputStream out)
			throws IOException, WebApplicationException {

		String res = "taskId: " + value.getTaskId() + ", title: " + value.getTitle();

		ReaderWriter.writeToAsString(res, out, mediaType);
	}

}

