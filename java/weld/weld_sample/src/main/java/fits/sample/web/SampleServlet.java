package fits.sample.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fits.sample.service.SampleService;

@WebServlet("/sample")
public class SampleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Inject
	private SampleService sample;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = response.getWriter();
		pw.println(sample.message("test"));
	}

}
