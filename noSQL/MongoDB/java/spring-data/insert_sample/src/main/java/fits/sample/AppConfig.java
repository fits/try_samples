package fits.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.document.mongodb.MongoTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoURI;

@Configuration
public class AppConfig {

	private @Value("#{mongodbProperties.uri}") String dbUri;
	private @Value("#{mongodbProperties.db}") String dbName;

	@Bean
	public Mongo mongo() throws Exception {
		return new Mongo(new MongoURI(dbUri));
	}

	@Bean
	public MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongo(), dbName);
	}
}

