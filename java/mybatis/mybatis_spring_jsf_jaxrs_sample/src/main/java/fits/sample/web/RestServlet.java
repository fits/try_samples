package fits.sample.web;

import javax.servlet.annotation.WebServlet;

@WebServlet("/rest/*")
public class RestServlet extends org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher {
	private static final long serialVersionUID = 1L;
}
