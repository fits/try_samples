package sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

@SpringBootApplication
@EnableLoadTimeWeaving
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
