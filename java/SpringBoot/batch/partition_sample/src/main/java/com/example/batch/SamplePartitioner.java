package com.example.batch;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SamplePartitioner implements Partitioner {
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        return Map.of(
            "A1", newContext("A1"),
            "B2", newContext("B2"),
            "C3", newContext("C3")
        );
    }

    private ExecutionContext newContext(String name) {
        var ctx = new ExecutionContext();

        ctx.putString("name", name);

        return ctx;
    }
}
