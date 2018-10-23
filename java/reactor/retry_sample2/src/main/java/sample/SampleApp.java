package sample;

import reactor.core.publisher.Flux;

public class SampleApp {
    public static void main(String... args) {

        try {
            retryTest();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private static void retryTest() {
        var f = Flux.just(0)
                .map(s -> Math.random())
                .log()
                .flatMap(v -> {
                    if (v > 0.7) {
                        return Flux.just(v);
                    }
                    return Flux.error(new Exception("value <= 0.7"));
                })
                .retry(5);

        System.out.println(f.blockFirst());
    }
}
