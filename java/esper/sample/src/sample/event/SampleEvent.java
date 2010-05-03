
package sample.event;

public class SampleEvent {

	private String name;
	private int point;

	public SampleEvent(String name, int point) {
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
