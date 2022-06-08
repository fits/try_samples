package com.example.data;

import org.seasar.doma.*;

import java.util.OptionalLong;

@Entity(immutable = true)
@Table(name = "tasks")
public record Task(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        OptionalLong id,
        String subject,
        Status status) {

        public enum Status { ready, completed }
}
