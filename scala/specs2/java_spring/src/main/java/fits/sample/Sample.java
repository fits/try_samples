package fits.sample;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class Sample {

	private @Value("#{sampleProperties.name}") String name;

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
