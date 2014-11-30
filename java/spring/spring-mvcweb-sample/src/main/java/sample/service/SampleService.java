package sample.service;

import org.springframework.stereotype.Service;

@Service
public class SampleService {
    public String sample(String id) {
        return id + "!!!";
    }
}
