package sample;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SampleController {

    @RequestMapping("/sample/{id}")
    public String sample(@PathVariable("id") String id, Model model) {
        model.addAttribute("id", "sid:" + id);

        return "sample";
    }
}
