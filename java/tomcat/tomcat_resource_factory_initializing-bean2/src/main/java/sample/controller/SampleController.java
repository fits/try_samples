package sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RequestMapping(value = "/app/{key}", method = RequestMethod.GET)
    @ResponseBody
    public String sample(@PathVariable String key) {
        return redisTemplate.boundValueOps(key).get();
    }
}
