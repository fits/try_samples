package sample.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public class SampleWebAppInitializer implements WebApplicationInitializer {
	@Override
	public void onStartup(ServletContext ctx) {
		initDispatcher(ctx);
	}

	private void initDispatcher(ServletContext ctx) {
		AnnotationConfigWebApplicationContext dispatchCtx = new AnnotationConfigWebApplicationContext();
		dispatchCtx.setConfigLocation("sample.config");

		ServletRegistration.Dynamic dispatcher = ctx.addServlet("dispatcher", new DispatcherServlet(dispatchCtx));

		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}
}
