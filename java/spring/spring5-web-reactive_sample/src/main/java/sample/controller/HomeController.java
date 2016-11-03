package sample.controller;

import lombok.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class HomeController {
    @RequestMapping("/")
    public Mono<String> home() {
        return Mono.just("home");
    }

    @RequestMapping(value = "/data/{n}", produces = "application/json")
    public Flux<Data> data(@PathVariable("n") String n) {
        return Flux.fromArray(new Data[] {
                new Data(n + "-A", 1),
                new Data(n + "-B", 2),
                new Data(n + "-C", 3)
        });
    }

    @Value
    class Data {
        private String name;
        private int value;
    }
}
