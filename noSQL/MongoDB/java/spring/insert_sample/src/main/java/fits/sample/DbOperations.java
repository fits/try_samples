package fits.sample;

public interface DbOperations {

	<T> T get(String key, Class<T> cls);

	<T> void put(String key, T obj);

}
