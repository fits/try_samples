package sample.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConsoleApp implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("ConsoleApp.run");
    }
}
