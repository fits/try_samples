package sample.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sample.service.SampleService;

@Component
public class ConsoleApp implements CommandLineRunner {
    @Autowired
    private SampleService service;

    @Override
    public void run(String... args) throws Exception {
        var res = service.process("data-1");
        System.out.println(res);
    }
}
