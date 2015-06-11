package sample.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import sample.service.Sample;

@Service("sample")
public class SampleImpl implements Sample {
    @Override
    public int call(String msg) {
        System.out.println("*** execute SampleImpl.call()");
        return msg.length();
    }
}
