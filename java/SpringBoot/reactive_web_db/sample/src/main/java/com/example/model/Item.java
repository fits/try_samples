package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

public record Item(@Id String id, long price) implements Persistable<String> {
    @Override
    public String getId() {
        return this.id();
    }

    @Override
    @JsonIgnore
    public boolean isNew() {
        return true;
    }
}
