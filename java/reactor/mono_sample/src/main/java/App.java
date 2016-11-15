import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

public class App {
    public static void main(String... args) throws IOException {

        System.out.println("--- (a) ---");

        Mono.just("(a)")
            .map(n -> {
                n = n + "-" + System.currentTimeMillis();
                dump(n + "_map1");
                return n;
            })
            .repeat(3)
            .map(n -> {
                dump(n + ":map2");
                sleep(2000);
                return n;
            })
            .subscribe(n -> dump(n + "_subscribe"));

        System.out.println("--- (b) ---");

        Mono.just("(b)")
            .map(n -> {
                n = n + "-" + System.currentTimeMillis();
                dump(n + "_map1");
                return n;
            })
            .publishOn(Schedulers.single())
            .repeat(3)
            .map(n -> {
                dump(n + ":map2");
                sleep(2000);
                return n;
            })
            .subscribe(n -> dump(n + "_subscribe"));

        System.out.println("--- (c) ---");

        Mono.just("(c)")
            .map(n -> {
                n = n + "-" + System.currentTimeMillis();
                dump(n + "_map1");
                return n;
            })
            .repeat(3)
            .publishOn(Schedulers.single())
            .map(n -> {
                dump(n + ":map2");
                sleep(2000);
                return n;
            })
            .subscribe(n -> dump(n + "_subscribe"));

        System.out.println("--- (d) ---");

        Mono.just("(d)")
            .map(n -> {
                n = n + "-" + System.currentTimeMillis();
                dump(n + "_map1");
                return n;
            })
            .repeat(3)
            .map(n -> {
                dump(n + ":map2");
                sleep(2000);
                return n;
            })
            .subscribeOn(Schedulers.single())
            .subscribe(n -> dump(n + "_subscribe"));

        System.out.println("--- (e) ---");

        Mono.just("(e)")
            .map(n -> {
                n = n + "-" + System.currentTimeMillis();
                dump(n + "_map1");
                return n;
            })
            .repeat(3)
            .parallel(3)
            .runOn(Schedulers.parallel())
            .map(n -> {
                dump(n + ":map2");
                sleep(2000);
                return n;
            })
            .subscribe(n -> dump(n + "_subscribe"));

        System.in.read();
    }

    private static void dump(String msg) {
        System.out.println(msg + ", thread: " + Thread.currentThread());
    }

    private static void sleep(int time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (Exception ex) {
        }
    }
}
