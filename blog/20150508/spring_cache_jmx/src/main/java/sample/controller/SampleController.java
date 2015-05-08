package sample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sample.service.SampleService;

import javax.inject.Inject;

@Controller
public class SampleController {
	@Inject
	private SampleService sampleService;

	@RequestMapping("/sample/{id}")
	@ResponseBody
	public String sample(@PathVariable("id") String id) {
		return sampleService.sample(id);
	}
}
