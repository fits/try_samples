package fits.sample;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

public class RoutingFilter implements Filter {
	private final static String ROUTING_PAGE = "index.php";

	public void init(FilterConfig config) {
	}

	//フィルター処理
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest)request;

		String contextPath = req.getContextPath();
		String requestPath = req.getRequestURI();

		String path = requestPath.replace(contextPath + "/", "").trim();

		if (!path.equals("") && !path.equals(ROUTING_PAGE)) {
			req.getRequestDispatcher("/" + ROUTING_PAGE + "?uri=" + path).forward(request, response);
			return;
		}

		chain.doFilter(request, response);
	}

	public void destroy() {
	}
}
