package sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;

@Configuration
@ComponentScan("sample.controller")
public class WebConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() throws NamingException {
        JndiObjectFactoryBean factory = new JndiObjectFactoryBean();

        factory.setJndiName("java:comp/env/redis/ConnectionFactory");

        factory.afterPropertiesSet();

        return (JedisConnectionFactory)factory.getObject();
    }

    @Bean
    public StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() throws NamingException {
        RedisTemplate<String, String> template = new RedisTemplate<>();

        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(stringRedisSerializer());
        template.setValueSerializer(stringRedisSerializer());

        return template;
    }
}
