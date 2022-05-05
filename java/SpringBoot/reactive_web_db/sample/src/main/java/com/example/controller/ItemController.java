package com.example.controller;

import com.example.model.Item;
import com.example.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class ItemController {
    @Autowired
    private ItemRepository repository;

    @GetMapping("/items/{id}")
    public Mono<Item> find(@PathVariable("id") String id) {
        return repository.findById(id);
    }

    @GetMapping("/items/search")
    public Flux<Item> findByValue(@RequestParam("price") long price) {
        return repository.findBy(price);
    }

    @PostMapping("/items")
    public Mono<String> create(@RequestBody ItemInput input) {
        String id = UUID.randomUUID().toString();
        var item = new Item(id, input.price());

        return repository.save(item).map(Item::id);
    }

    private record ItemInput(long price){}
}
