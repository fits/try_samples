package sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.service.AsyncService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
public class AsyncController {
    @Autowired
    private AsyncService asyncService;

    @RequestMapping("/sample")
    public String sample() throws InterruptedException, ExecutionException, TimeoutException {
        System.out.println("*** async-sample: " + Thread.currentThread());

        return asyncService.exec().get(10, TimeUnit.SECONDS);
    }
}
