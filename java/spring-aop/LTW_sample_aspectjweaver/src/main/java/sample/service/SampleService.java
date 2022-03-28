package sample.service;

import org.springframework.stereotype.Service;

@Service
public class SampleService {
    public String process(String value) {
        return internalProcess(value);
    }

    private String internalProcess(String value) {
        return "processed:" + value;
    }
}
