package com.example.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ConcurrentHashMap<String, Item> store = new ConcurrentHashMap<>();

    @GetMapping("{id}")
    public Mono<Item> getItem(@PathVariable("id") String id) {
        return Mono.justOrEmpty(store.get(id))
                .switchIfEmpty(Mono.error(new ItemNotExistsException(id)));
    }

    @PutMapping
    public Mono<String> putItem(@RequestBody CreateItem input) {
        return Mono.justOrEmpty(input.id())
                .switchIfEmpty(Mono.error(new RequiredFieldException("id")))
                .map(id -> new Item(id, input.name(), 0))
                .flatMap(this::putItem);
    }

    private Mono<String> putItem(Item item) {
        var id = item.id();

        return Mono.justOrEmpty(store.putIfAbsent(id, item))
                .flatMap(_old -> Mono.<String>error(new ItemExistsException(id)))
                .switchIfEmpty(Mono.just(id));
    }

    private record CreateItem(String id, String name) {}

    private record Item(String id, String name, int value) {}

    private class RequiredFieldException extends AppException {
        RequiredFieldException(String name) {
            super("required field=" + name);
        }
    }

    private class ItemExistsException extends AppException {
        ItemExistsException(String id) {
            super("exists id=" + id);
        }
    }

    private class ItemNotExistsException extends AppException {
        ItemNotExistsException(String id) {
            super("not exists id=" + id);
        }
    }
}
