package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<ItemInput, Item> sample() {
        return input -> new Item("item-" + input.value(), input.value());
    }

    public record ItemInput(int value) {}
    public record Item(String id, int value) {}
}
