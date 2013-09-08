package fits.sample;

import org.jboss.weld.environment.se.events.ContainerInitialized;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class App {

	@Inject
	SampleWrapper sampleWrapper;

	public void run(@Observes ContainerInitialized event) {
		System.out.println("***** run");
		sampleWrapper.test();
	}
}
