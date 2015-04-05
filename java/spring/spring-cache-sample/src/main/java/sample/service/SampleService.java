package sample.service;

import org.springframework.stereotype.Service;

import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import java.util.Arrays;
import java.util.List;
import java.util.Date;

@Service
public class SampleService {
    @CacheResult(cacheName = "sample")
    public List<String> sample(String id, int type) {
        System.out.println("*** call sample method: " + id + ", " + type);

        return Arrays.asList(new Date().toString());
    }

    /* 下記のように引数が同じで戻り値の型が異なるメソッドで
     * 同じキャッシュを使うのは危険
     * 
     * sample を実行してキャッシュを作った後に、
     * 同じ引数で sample2 を呼び出すと ClassCastException が発生する
     */
    @CacheResult(cacheName = "sample")
    public String sample2(String id, int type) {
        System.out.println("*** call sample2 method: " + id + ", " + type);

        return now(id);
    }

    @CacheRemoveAll(cacheName = "sample")
    public void clear() {
    }

    // 下記にはキャッシュが適用されない
    @CacheResult(cacheName = "sample")
    private String now(String id) {
        System.out.println("***** call now: " + id);
        return id + System.currentTimeMillis();
    }
}
