package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import sample.repository.SampleRepository;

@ComponentScan
@EnableAutoConfiguration
public class App implements CommandLineRunner {
	@Autowired
	private SampleRepository sampleRepository;

	@Override
	public void run(String... args) {
		String key = "a1";

		sampleRepository.update(key, v -> 10);
		System.out.println(sampleRepository.get(key));

		sampleRepository.updateWithCas1(key, v -> v + 5);
		System.out.println(sampleRepository.get(key));

		sampleRepository.updateWithCas2(key, v -> v + 8);
		System.out.println(sampleRepository.get(key));
	}

	public static void main(String... args) {
		SpringApplication.run(App.class, args);
	}
}
