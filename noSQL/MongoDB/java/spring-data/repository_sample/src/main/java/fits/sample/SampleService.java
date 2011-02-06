package fits.sample;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SampleService {

	@Autowired
	private DataRepository rep;

	public void addData(Iterable<Data> list) {
		rep.save(list);
	}

	public List<Data> getData(String name) {
		return rep.findByName(name);
	}

	public List<Data> findData(String name, int point) {
		return rep.findByNameLikeAndPointGreaterThan(name, point);
	}
}
