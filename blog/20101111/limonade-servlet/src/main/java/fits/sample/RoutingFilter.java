package fits.sample;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

public class RoutingFilter implements Filter {
	private final static String ROUTING_PAGE_PARAM_NAME = "routing-page";
	private String routingPage = "index.php";

	public void init(FilterConfig config) {
		String paramRoutingPage = config.getInitParameter(ROUTING_PAGE_PARAM_NAME);
		if (paramRoutingPage != null) {
			this.routingPage = paramRoutingPage;
		}
	}

	//ルーティング制御
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest)request;

		String contextPath = req.getContextPath();
		String requestPath = req.getRequestURI();

		String path = requestPath.replace(contextPath + "/", "").trim();

		// ルート / や /index.php へのアクセスはフォワードしないようにする
		if (!path.equals("") && !path.equals(this.routingPage)) {
			req.getRequestDispatcher("/" + this.routingPage + "?uri=" + path).forward(request, response);
			return;
		}

		chain.doFilter(request, response);
	}

	public void destroy() {
	}
}
