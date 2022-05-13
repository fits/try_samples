package com.example.sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sample2")
public class Sample2Controller {
    @Autowired
    private Sample2Info info;

    @GetMapping("/info")
    public Mono<Sample2Info> info() {
        return Mono.just(info);
    }
}
