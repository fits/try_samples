package sample.service;

import org.springframework.stereotype.Service;

@Service
public interface Sample {
    int call(String msg);
}
