package json.sample.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import json.sample.data.Stock;

import java.util.HashMap;
import java.util.Map;

@Controller("/stocks")
public class StockController {
    private Map<String, Stock> repo = new HashMap<>();

    @Get("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Stock find(String id) {
        return repo.get(id);
    }

    @Post
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Stock store(@Body Stock stock) {
        repo.put(stock.getId(), stock);
        return stock;
    }
}
