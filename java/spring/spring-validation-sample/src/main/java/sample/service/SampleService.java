package sample.service;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SampleService {
    public String sample(String id, int type) {
        System.out.println("*** call sample method: " + id + ", " + type);

        return new Date().toString();
    }
}
