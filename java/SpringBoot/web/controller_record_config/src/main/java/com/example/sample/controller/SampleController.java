package com.example.sample.controller;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sample")
@EnableConfigurationProperties(SampleController.Context.class)
public class SampleController {
    private Context context;

    public SampleController(Context context) {
        this.context = context;
    }

    @GetMapping("/info")
    public Mono<Context> info() {
        System.out.println(context);
        return Mono.justOrEmpty(context);
    }

    @ConfigurationProperties(prefix = "sample")
    record Context(int id, String name, String note) {}
}
