package com.example.sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sample1")
public class Sample1Controller {
    @Autowired
    private Sample1Info info;

    @GetMapping("/info")
    public Mono<Sample1Info> info() {
        return Mono.just(info);
    }
}
