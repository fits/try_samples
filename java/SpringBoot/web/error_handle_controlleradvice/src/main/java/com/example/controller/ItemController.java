package com.example.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ConcurrentHashMap<String, Item> store = new ConcurrentHashMap<>();

    @GetMapping("{id}")
    public Item getItem(@PathVariable("id") String id) {
        return Optional.ofNullable(store.get(id))
                .orElseThrow(() -> new ItemNotExistsException(id));
    }

    @PutMapping
    public String putItem(@RequestBody CreateItem input) {
        var id = Optional.ofNullable(input.id())
                .orElseThrow(() -> new RequiredFieldException("id"));

        var item = new Item(id, input.name(), 0);

        if (store.putIfAbsent(id, item) != null) {
            throw new ItemExistsException(id);
        }

        return id;
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
