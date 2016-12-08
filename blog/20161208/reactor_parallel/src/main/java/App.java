import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.invoke.MethodHandle;

import static java.lang.invoke.MethodHandles.publicLookup;
import static java.lang.invoke.MethodType.methodType;

public class App {
    public static void main(String... args) throws Throwable {
        MethodHandle mh = publicLookup().findStatic(
            App.class, 
            "sample" + args[0], 
            methodType(void.class)
        );

        mh.invoke();

        System.in.read();
    }

    public static void sampleA() {
        Mono.just("(A)")
            .repeat(3)
            .map(n -> {
                printThread(n + "_map");
                return n;
            })
            .subscribe(n -> printThread(n + "_subscribe"));
    }

    public static void sampleB() {
        Mono.just("(B)")
            .repeat(3)
            .publishOn(Schedulers.parallel())
            //.publishOn(Schedulers.single())
            .map(n -> {
                printThread(n + "_map");
                return n;
            })
            .subscribe(n -> printThread(n + "_subscribe"));
    }

    public static void sampleC() {
        Mono.just("(C)")
            .repeat(3)
            .parallel()
            .runOn(Schedulers.parallel())
            .map(n -> {
                printThread(n + "_map");
                return n;
            })
            .subscribe(n -> printThread(n + "_subscribe"));
    }

    private static void printThread(String msg) {
        System.out.println(msg + ", thread: " + Thread.currentThread());
    }
}
