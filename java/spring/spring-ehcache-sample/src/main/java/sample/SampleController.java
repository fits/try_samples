package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import javax.cache.annotation.CacheRemoveAll;

@Controller
public class SampleController {
    @Autowired
    private SampleService service;


    @RequestMapping("/sample/{id}/{type}")
    public String sample(@PathVariable("id") String id, @PathVariable("type") int type, Model model) {

        model.addAttribute("data", service.sample(id, type));
        return "sample";
    }

    @RequestMapping("/clear")
    @CacheRemoveAll(cacheName = "sample")
    public String clear() {
        return "clear";
    }
}
