
package fits.sample;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public class TestService {

	@ServiceActivator(inputChannel="input", outputChannel="output")
	public String hello(String name) {
		return "hello " + name;
	}
}
