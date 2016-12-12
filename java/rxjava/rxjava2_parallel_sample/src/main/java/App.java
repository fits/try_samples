import io.reactivex.Single;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

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
        Single.just("(A)")
            .repeat(3)
            .map(n -> {
                printThread(n + "_map");
                return n;
            })
            .subscribe(n -> printThread(n + "_subscribe"));
    }

    public static void sampleB() {
        Single.just("(B)")
            .repeat(3)
            .observeOn(Schedulers.newThread())
            //.observeOn(Schedulers.single())
            .map(n -> {
                printThread(n + "_map");
                return n;
            })
            .subscribe(n -> printThread(n + "_subscribe"));
    }

    public static void sampleC() {
        Single.just("(C)")
            .repeat(3)
            .flatMap(s ->
                Flowable.just(s)
                    .map(n -> {
                        printThread(n + "_map");
                        return n;
                    })
                    .subscribeOn(Schedulers.computation())

            )
            .subscribe(n -> printThread(n + "_subscribe"));
    }

    public static void sampleD() {
        Single.just("(D)")
            .repeat(3)
            .flatMap(s ->
                Flowable.just(s)
                    .map(n -> {
                        printThread(n + "_map");
                        return n;
                    })
                    .map(n -> {
                        printThread(n + "_subscribe");
                        return n;
                    })
                    .subscribeOn(Schedulers.computation())

            )
            .subscribe();
    }


    private static void printThread(String msg) {
        System.out.println(msg + ", thread: " + Thread.currentThread());
    }
}
