package sample;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Date;
import javax.cache.annotation.CacheResult;

@Service
public class SampleService {
    @CacheResult(cacheName = "sample")
    public List<String> sample(String id, int type) {
        System.out.println("*** call sample method: " + id + ", " + type);

        return Arrays.asList(id + "/" + type, new Date().toString());
    }
}
