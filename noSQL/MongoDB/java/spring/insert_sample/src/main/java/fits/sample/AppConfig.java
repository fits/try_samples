package fits.sample;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.ApplicationContext;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;


@Configuration
public class AppConfig {

	private String dbUri = "mongodb://localhost/";

	@Autowired
	private DbOperations mongoDbOperations;

	public void setDbUri(String dbUri) {
		this.dbUri = dbUri;
	}

	@Bean
	public Mongo mongo() throws Exception {
		System.out.println("****** " + dbUri);
		
		return new Mongo(new MongoURI(dbUri));
	}

	@Bean
	public DbOperations mongoDbOperations() {
		return mongoDbOperations;
	}
}

