package sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sample.service.SampleService;

import java.util.Arrays;
import javax.inject.Inject;

@Controller
public class SampleController {
    @Inject
    private SampleService service;

    @RequestMapping("/sample/{id}/{type}")
    public String sample(@PathVariable("id") String id, @PathVariable("type") int type, Model model) {

        model.addAttribute("data", service.sample(id, type));
        return "sample";
    }

    @RequestMapping("/sample2/{id}/{type}")
    public String sample2(@PathVariable("id") String id, @PathVariable("type") int type, Model model) {

        model.addAttribute("data", Arrays.asList(service.sample2(id, type)));
        return "sample";
    }

    @RequestMapping("/clear")
    public String clear() {
        service.clear();
        return "clear";
    }
}
