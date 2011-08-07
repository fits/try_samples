package fits.sample;

import com.google.inject.*;

public class App {
	public static void main( String[] args ) {
		Injector inj = Guice.createInjector(new AbstractModule() {
			protected void configure() {
				bind(SampleService.class).to(SampleServiceImpl.class);
			}
		});

		SampleClient client = inj.getInstance(SampleClient.class);
		client.print("1");

		inj.getInstance(SampleClient.class).print("2");
	}
}
