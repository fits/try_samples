package sample.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@RestController
public class CallableController {

    @RequestMapping("/sample")
    public Callable<String> sample() {
        Callable<String> res = () -> {
            Thread.sleep(2000);
            return "callable-sample: " + Thread.currentThread();
        };

        System.out.println("*** callable-sample: " + Thread.currentThread());

        return res;
    }
}
