package sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class DebugSampleController {
	@RequestMapping("/debug/sample/{id}")
	@ResponseBody
	public String sample(@PathVariable("id") String id) {
		return "debug-sample: " + id + ", " + new Date();
	}
}
