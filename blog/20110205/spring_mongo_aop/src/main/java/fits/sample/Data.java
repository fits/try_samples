package fits.sample;

//データクラス
public class Data {

	private String name;
	private int point;

	public Data() {
		this(null, 0);
	}

	public Data(String name, int point) {
		this.name = name;
		this.point = point;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getPoint() {
		return this.point;
	}
}
