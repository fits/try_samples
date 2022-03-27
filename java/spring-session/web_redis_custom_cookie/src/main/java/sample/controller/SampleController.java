package sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Optional;

@RestController
public class SampleController {
    private static final String DATA = "data";

    @Autowired
    private HttpSession session;

    @GetMapping("/set/{value}")
    public Data set(@PathVariable("value") String value) {
        var data = new Data(value);
        session.setAttribute(DATA, data);

        return data;
    }

    @GetMapping("/get")
    public Optional<Data> get() {
        var data = (Data)session.getAttribute(DATA);
        session.invalidate();

        return Optional.ofNullable(data);
    }

    public record Data(String value) implements Serializable {}
}
