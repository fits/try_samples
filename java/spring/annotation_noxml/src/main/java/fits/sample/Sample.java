package fits.sample;

import org.springframework.stereotype.Component;

@Component
public class Sample {

	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void printMessage() {
		System.out.println("sample : " + this.name);
	}
}
