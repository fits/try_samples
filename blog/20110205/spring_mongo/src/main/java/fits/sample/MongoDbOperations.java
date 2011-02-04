package fits.sample;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

//MongoDB用のDB操作クラス
@Component
public class MongoDbOperations implements DbOperations {

	@Autowired
	private DB db;

	public <T> T get(String key, Class<T> cls) {
		T result = null;

		DBObject obj = db.getCollection(key).findOne();

		if (obj != null) {
			String json = JSON.serialize(obj);
			result = net.arnx.jsonic.JSON.decode(json, cls);
		}

		return result;
	}

	public <T> void put(String key, T obj) {
		DBCollection col = db.getCollection(key);
		col.drop();

		String json = net.arnx.jsonic.JSON.encode(obj);
		col.insert((DBObject)JSON.parse(json));
	}

}
