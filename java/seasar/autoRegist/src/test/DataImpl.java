package test;

public class DataImpl implements Data {

	private String name;
	private int point;

	public DataImpl() {
		this.name = "test";
		this.point = 100;
	}

	public DataImpl(String name, int point) {
		this.name = name;
		this.point = point;
	}

	public String getName() {
		return this.name;
	}

	public int getPoint() {
		return this.point;
	}

}