package sample.controller;

import lombok.Value;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class SampleController {

    @RequestMapping("/")
    public Mono<String> sample() {
        return Mono.just("sample");
    }

    @RequestMapping(value = "/data/{name}", produces = "application/json")
    public Flux<Data> dataList(@PathVariable("name") String name) {
        return Flux.fromArray(new Data[] {
            new Data(name + "-1", 1),
            new Data(name + "-2", 2),
            new Data(name + "-3", 3)
        });
    }

    @Value
    class Data {
        private String name;
        private int value;
    }
}
