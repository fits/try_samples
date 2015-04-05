package sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DevSampleController {

    @RequestMapping("/dev/sample/{id}")
    public String sample(@PathVariable("id") String id, Model model) {
        System.out.println("#### dev-sample : " + id);

        model.addAttribute("data", "dev-sample:" + id);
        return "sample";
    }
}
