package fits.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.MongoURI;

//設定クラス
@Configuration
public class AppConfig {

	private @Value("#{mongodbProperties.uri}") String dbUri;
	private @Value("#{mongodbProperties.db}") String dbName;

	@Bean
	public Mongo mongo() throws Exception {
		return new Mongo(new MongoURI(dbUri));
	}

	@Bean
	public DB mongoDb() throws Exception {
		return mongo().getDB(dbName);
	}
}

