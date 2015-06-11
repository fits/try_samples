package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sample.service.Sample;

@SpringBootApplication
public class App implements CommandLineRunner {
    @Autowired
    //@Qualifier("anotherSample")
    private Sample sample;

    @Override
    public void run(String... args) {
        System.out.println("*** call: " + sample.call("aaa"));
    }

    public static void main(String... args) {
        SpringApplication.run(App.class, args);
    }
}
