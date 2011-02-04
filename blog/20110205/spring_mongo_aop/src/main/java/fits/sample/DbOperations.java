package fits.sample;

//DB操作インターフェース
public interface DbOperations {

	//データを取得する
	<T> T get(String key, Class<T> cls);

	//データを設定する
	<T> void put(String key, T obj);

}
