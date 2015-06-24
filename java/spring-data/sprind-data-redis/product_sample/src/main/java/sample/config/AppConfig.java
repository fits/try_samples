package sample.config;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import sample.model.Product;

@Configuration
public class AppConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        val factory = new JedisConnectionFactory();
        factory.setUsePool(true);

        return factory;
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, Product> redisTemplate() {
        val template = new RedisTemplate<String, Product>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(stringRedisSerializer());

        return template;
    }
}
