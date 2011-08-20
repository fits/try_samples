package fits.sample;

import java.util.HashMap;
import java.util.Map; 

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.persist.PersistFilter;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.guice.JerseyServletModule;

import fits.sample.dao.TaskDao;
import fits.sample.dao.impl.TaskDaoImpl;

public class SampleGuiceConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(
			new JerseyServletModule() {
				@Override
				protected void configureServlets() {
					install(new JpaPersistModule("sample"));

					bind(TaskDao.class).to(TaskDaoImpl.class);

					Map<String, String> params = new HashMap();
					params.put(PackagesResourceConfig.PROPERTY_PACKAGES, "fits.sample.ws");
					serve("/*").with(GuiceContainer.class, params);

					//PersistService を start させるための設定
					filter("/*").through(PersistFilter.class);
				}
			}
		);
	}
}
