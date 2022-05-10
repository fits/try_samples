package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
public class ItemResource {
    @GET
    public List<Item> items() {
        return List.of(
            new Item("item-1", 1),
            new Item("アイテム2", 2)
        );
    }

    record Item(String name, int value) {}
}
