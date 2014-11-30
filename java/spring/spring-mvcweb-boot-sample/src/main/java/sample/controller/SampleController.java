package sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import sample.service.SampleService;

@Controller
public class SampleController {
    @Autowired
    private SampleService sampleService;

    @RequestMapping(value = "/sample/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String sample(@PathVariable String id) {
        String res = sampleService.sample(id);
        System.out.println("res : " + res);
        return res;
    }
}
