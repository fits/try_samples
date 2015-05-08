package sample.config;

import net.sf.ehcache.management.ManagementService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableCaching
public class WebConfig {
	@Bean
	public CacheManager cacheManager() {
		EhCacheCacheManager manager = new EhCacheCacheManager();
		manager.setCacheManager(ehcache().getObject());

		return manager;
	}

	@Bean
	public EhCacheManagerFactoryBean ehcache() {
		EhCacheManagerFactoryBean ehcache = new EhCacheManagerFactoryBean();
		ehcache.setConfigLocation(new ClassPathResource("ehcache.xml"));

		return ehcache;
	}

	@Bean
	public MBeanServerFactoryBean mbeanServer() {
		MBeanServerFactoryBean factory = new MBeanServerFactoryBean();
		factory.setLocateExistingServerIfPossible(true);

		return factory;
	}

	@Bean
	public ManagementService managementService() {
		ManagementService service = new ManagementService(ehcache().getObject(), mbeanServer().getObject(), true, true, true, true);
		service.init();

		return service;
	}
}
