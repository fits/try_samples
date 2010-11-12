package fits.sample;

import java.io.File;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

public class CakeRoutingFilter implements Filter {
	private ServletContext ctx;

	public void init(FilterConfig config) {
		this.ctx = config.getServletContext();
	}

	//フィルター処理
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

		HttpServletRequest req = (HttpServletRequest)request;

		String contextPath = req.getContextPath();
		String requestPath = req.getRequestURI();

		String path = requestPath.replace(contextPath + "/", "").trim();

		if (!path.startsWith("app/webroot")) {

			String vPath = "app/webroot/" + path;
			String rPath = this.ctx.getRealPath(vPath);

			if (new File(rPath).exists()) {
				this.dispatchForward("/" + vPath, request, response);
				return;
			}
			else {
				this.dispatchForward("/app/webroot/index.php?url=" + path, request, response);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	public void destroy() {
	}

	private void dispatchForward(String path, ServletRequest request, ServletResponse response) throws ServletException, IOException {

		request.getRequestDispatcher(path).forward(request, response);
	}
}
