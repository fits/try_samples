package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import sample.form.Data;
import sample.service.SampleService;

@Controller
public class SampleController {
    @Autowired
    private SampleService service;

    @RequestMapping("/sample")
    public String sample(@Validated Data d, Model model) {

        model.addAttribute("data", service.sample(d.getId(), d.getType()));
        return "sample";
    }

    @RequestMapping("/sample2")
    public String sample2(@Validated Data d, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            System.out.println("*** ERROR" + bindingResult.getAllErrors());

            return "sample2";
        }

        model.addAttribute("data", service.sample(d.getId(), d.getType()));
        return "sample";
    }
}
