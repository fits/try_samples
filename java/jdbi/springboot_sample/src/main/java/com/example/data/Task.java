package com.example.data;

public record Task(long id, String subject, Status status) {
    public enum Status { ready, completed }
}
