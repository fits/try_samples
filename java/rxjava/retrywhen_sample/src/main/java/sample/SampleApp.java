package sample;

import io.reactivex.Flowable;
import java.util.concurrent.TimeUnit;

public class SampleApp {
    public static void main(String... args) {

        var res = Flowable.just("one")
                .map(SampleApp::log)
                .flatMap(s -> Flowable.just("two"))
                .map(SampleApp::log)
                .flatMap(s -> {
                    throw new RuntimeException();
                })
                .retryWhen(errs ->
                        errs.take(2)
                                .delay(3, TimeUnit.SECONDS)
                )
        ;

        res.blockingSubscribe();
    }

    private static String log(String v) {
        System.out.println(v);
        return v;
    }
}
