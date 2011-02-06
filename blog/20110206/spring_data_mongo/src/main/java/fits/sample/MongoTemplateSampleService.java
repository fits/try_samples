package fits.sample;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.document.mongodb.MongoTemplate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

@Service
public class MongoTemplateSampleService implements SampleService {

	@Autowired
	private MongoTemplate temp;

	//Data を追加する
	public void addData(List<Data> list) {
		temp.insertList(list);
	}

	//指定名の Data を取得する
	public List<Data> getData(String name) {
		BasicDBObject query = new BasicDBObject("name", name);
		return temp.query(query, Data.class);
	}

	//指定名を含み、指定ポイントより大きい Data を取得する
	public List<Data> findData(String name, int point) {

		DBObject query = QueryBuilder.start("name").regex(Pattern.compile(".*" + name + ".*")).and("point").greaterThan(point).get();

		return temp.query(query, Data.class);
	}
}
