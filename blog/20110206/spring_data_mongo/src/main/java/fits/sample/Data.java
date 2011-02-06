package fits.sample;

import java.math.BigInteger;

//データクラス
public class Data {

	private BigInteger id;
	private String name;
	private int point;

	public Data() {
		this(null, 0);
	}

	public Data(String name, int point) {
		this.name = name;
		this.point = point;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public BigInteger getId() {
		return this.id;
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
