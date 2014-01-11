package sample;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.ognl.Ognl;
import org.apache.commons.ognl.ClassResolver;
import org.apache.commons.ognl.OgnlException;

@WebServlet(urlPatterns = {"/sample2"})
public class OgnlSample2Servlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Sample data = new Sample();
		data.getLines().add(new Item(1, new BigDecimal("100")));
		data.getLines().add(new Item(2, new BigDecimal("200")));
		data.getLines().add(new Item(3, new BigDecimal("300")));

		try {
			Map<String, Object> ctx = Ognl.createDefaultContext(null, new SampleClassResolver());

			res.getWriter().print("res = " + Ognl.getValue("lines.{ #this.code == 2 }", ctx, data));
		} catch (OgnlException ex) {
			throw new ServletException(ex);
		}
	}

	class SampleClassResolver implements ClassResolver {
		@Override
		public Class<?> classForName( String className, Map<String, Object> context ) throws ClassNotFoundException {
			return Class.forName(className);
		}
	}
}