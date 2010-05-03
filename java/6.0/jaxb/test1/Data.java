
import javax.xml.bind.annotation.*;

@XmlRootElement
public class Data<T> {

	private String name;
	private int point;
	private Item item;
	private T value;

	public Data() {
	}

	public Data(String name, int point) {
		this.name = name;
		this.point = point;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPoint() {
		return this.point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public Item getDetail() {
		return this.item;
	}

	public void setDetail(Item item) {
		this.item = item;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}