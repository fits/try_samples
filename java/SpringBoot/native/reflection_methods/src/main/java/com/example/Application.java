package com.example;

import static org.springframework.nativex.hint.TypeAccess.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.nativex.hint.TypeHint;

@TypeHint(typeNames = "com.example.Task$Sample1", access = QUERY_DECLARED_METHODS)
@TypeHint(typeNames = "com.example.Task$Sample2", access = { QUERY_DECLARED_METHODS, DECLARED_METHODS })
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
