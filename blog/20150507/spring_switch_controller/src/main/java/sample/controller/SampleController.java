package sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {
	@RequestMapping("/sample/{id}")
	@ResponseBody
	public String sample(@PathVariable("id") String id) {
		return "sample: " + id + ", " + System.currentTimeMillis();
	}
}
