package fits.sample;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SampleService {

	@Autowired
	private DataRepository rep;

	//Data を追加する
	public void addData(Iterable<Data> list) {
		rep.save(list);
	}

	//指定名の Data を取得する
	public List<Data> getData(String name) {
		return rep.findByName(name);
	}

	//指定名を含み、指定ポイントより大きい Data を取得する
	public List<Data> findData(String name, int point) {
		return rep.findByNameLikeAndPointGreaterThan(name, point);
	}
}
