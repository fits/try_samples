package sample;

import org.springframework.stereotype.Service;

@Service
public class LogService {
    public void log(String msg) {
        System.out.println(msg);
    }
}
