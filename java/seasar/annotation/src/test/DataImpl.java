package test;

import org.seasar.framework.container.annotation.tiger.*;

@Component(name="data")
public class DataImpl implements Data {

	private String name;
	private int point;

	public DataImpl() {
		this("test", 100);
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