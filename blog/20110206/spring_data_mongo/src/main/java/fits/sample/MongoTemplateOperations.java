package fits.sample;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.document.mongodb.MongoTemplate;

@Component
public class MongoTemplateOperations implements DbOperations {

	@Autowired
	private MongoTemplate db;

	public <T> T get(String key, Class<T> cls) {
		List<T> result = db.getCollection(key, cls);

		return (result.size() > 0)? result.get(0): null;
	}

	public <T> void put(String key, T obj) {
		db.dropCollection(key);
		db.insert(key, obj);
	}
}
