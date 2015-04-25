package sample;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableCaching
public class WebConfig {
    @Bean
    public CacheManager cacheManager() {
        EhCacheCacheManager res = new EhCacheCacheManager();

        EhCacheManagerFactoryBean ehcache = new EhCacheManagerFactoryBean();
        ehcache.setConfigLocation(new ClassPathResource("ehcache.xml"));

        res.setCacheManager(ehcache.getObject());

        return res;
    }
}
