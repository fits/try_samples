package sample.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import sample.service.Sample;

@Primary
@Service("anotherSample")
public class AnotherSampleImpl implements Sample {
    @Override
    public int call(String msg) {
        System.out.println("--- execute AnotherSampleImpl.call()");
        return 1000;
    }
}
