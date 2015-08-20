package sample.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import redis.clients.util.JedisURIHelper;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

@Configuration
@EnableScheduling
@PropertySource("classpath:app-${spring.profiles.active:dev}.properties")
public class AppConfig {
	@Value("db-${spring.profiles.active:dev}.properties")
	private String dbConfigFile;

	@Value("${sample.redis.uri}")
	private URI redisUri;

	@Bean
	public Properties dbConfig() throws IOException {
		Properties config = new Properties();
		config.load(ClassLoader.getSystemResourceAsStream(dbConfigFile));
		return config;
	}

	@Bean
	public DataSource dataSource() throws Exception {
		return new HikariDataSource(new HikariConfig(dbConfig()));
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public JdbcTemplate jdbcTemplate() throws Exception {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public StringRedisSerializer stringRedisSerializer() {
		return new StringRedisSerializer();
	}

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory result = new JedisConnectionFactory();

		result.setHostName(redisUri.getHost());
		result.setPort(redisUri.getPort());
		result.setDatabase(JedisURIHelper.getDBIndex(redisUri));

		String password = JedisURIHelper.getPassword(redisUri);

		if (password != null) {
			result.setPassword(password);
		}

		result.setUsePool(true);

		return result;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate() {
		RedisTemplate<String, String> result = new RedisTemplate<>();

		result.setConnectionFactory(jedisConnectionFactory());

		result.setKeySerializer(stringRedisSerializer());
		result.setHashKeySerializer(stringRedisSerializer());

		return result;
	}
}
