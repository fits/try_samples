package sample;

import org.springframework.stereotype.Service;

@Service
public class DataService {
    public Data find(String id) {
        return new Data(id, "sample");
    }
}
