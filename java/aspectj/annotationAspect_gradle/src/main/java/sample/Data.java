package sample;

import sample.annotation.*;

public class Data {

	private String name;

	@CheckPoint(name="getter")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}