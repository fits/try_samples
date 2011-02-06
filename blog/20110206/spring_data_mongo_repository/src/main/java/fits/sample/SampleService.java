package fits.sample;

import java.util.List;

public interface SampleService {

	//Data を追加する
	void addData(List<Data> list);

	//指定名の Data を取得する
	List<Data> getData(String name);

	//指定名を含み、指定ポイントより大きい Data を取得する
	List<Data> findData(String name, int point);
}
