package sample;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

@Configuration
@EnableWebMvc
@EnableCaching
public class WebConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager res = new SimpleCacheManager();

        res.setCaches(Arrays.asList(
            new ConcurrentMapCache("sample")
        ));

        return res;
    }
}
