package test;

public class Data {

	private String name;

	@CheckPoint(name="test")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}