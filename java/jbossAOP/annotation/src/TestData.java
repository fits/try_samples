
import java.util.*;

public class TestData<T, S> {

	private Map<T, S> map;

	public TestData() {
		this.map = this.createMap();
	}

	protected Map<T, S> createMap() {
		return new HashMap<T, S>();
	}

	public void addData(T name, S data) {
		this.map.put(name, data);
	}

	public void printData() {
		for (Map.Entry<T, S> entry : this.map.entrySet()) {
			System.out.println("key : " + entry.getKey() + ", value : " + entry.getValue());
		}
	}
}