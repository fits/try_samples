package sample;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.ognl.Ognl;
import org.apache.commons.ognl.OgnlException;

@WebServlet(urlPatterns = {"/sample1"})
public class OgnlSample1Servlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		Sample data = new Sample();
		data.getLines().add(new Item(1, new BigDecimal("100")));
		data.getLines().add(new Item(2, new BigDecimal("200")));
		data.getLines().add(new Item(3, new BigDecimal("300")));

		try {
			/* â∫ãLÇÃèàóùÇÕÉGÉâÅ[Ç™î≠ê∂
			 *
			 *  Caused by: java.lang.ClassNotFoundException:
		  	 *    Unable to resolve class: javassist.ClassPool
		  	 */
			res.getWriter().print("res = " + Ognl.getValue("lines.{ #this.code == 2 }", data));
		} catch (OgnlException ex) {
			throw new ServletException(ex);
		}
	}
}