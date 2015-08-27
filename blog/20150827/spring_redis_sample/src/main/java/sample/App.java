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

		sampleRepository.save(key, 10);
		Object res1 = sampleRepository.updateWithCas1(key, v -> v + 5);
		System.out.println("res1 = " + res1);
		System.out.println(sampleRepository.load(key));

		sampleRepository.save(key, 10);
		Object res2 = sampleRepository.updateWithCas2(key, v -> v + 10);
		System.out.println("res2 = " + res2);
		System.out.println(sampleRepository.load(key));

		sampleRepository.save(key, 10);
		Object res3 = sampleRepository.updateWithCas3(key, v -> v + 15);
		System.out.println("res3 = " + res3);
		System.out.println(sampleRepository.load(key));
	}

	public static void main(String... args) {
		SpringApplication.run(App.class, args);
	}
}
