package com.example;

import static org.springframework.nativex.hint.TypeAccess.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.ResourceHint;
import org.springframework.nativex.hint.TypeHint;

import java.util.function.Function;

@ResourceHint(patterns = "org/joda/time/tz/data/.*")
@TypeHint(types = Application.Item.class, access = { DECLARED_CONSTRUCTORS, DECLARED_METHODS })
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        var handler = args.length > 0 ? args[0] : "sample";
        System.setProperty("_HANDLER", handler);

        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Function<ItemInput, Item> sample() {
        return input -> new Item("item-" + input.value(), input.value());
    }

    public record ItemInput(int value) {}
    public record Item(String id, int value) {}
}
