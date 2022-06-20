package com.example;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.function.UnaryOperator;

@Component
public class Task implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (var s : test().toIterable()) {
            System.out.println(s);
        }
    }

    private Flux<String> test() {
        return Flux.push(sink -> {
            showMethods(sink, new Sample1());
            showMethods(sink, new Sample2("sample2", 1));

            UnaryOperator<String> sample3 = s -> "sample3-" + s;
            showMethods(sink, sample3);

            sink.complete();
        });
    }

    private <T> void showMethods(FluxSink<String> sink, T obj) {
        try {
            var ms = obj.getClass().getDeclaredMethods();

            if (ms.length == 0) {
                sink.next("*** WARN [ " + obj.getClass() + " ]: no methods");
                return;
            }

            for (var m : ms) {
                sink.next("[ " + obj.getClass() + " ]: " + m);
            }
        } catch (Exception ex) {
            sink.error(ex);
        }
    }

    private static class Sample1 {
        public String method1(int value) {
            method2();
            return "sample1:" + value;
        }

        private void method2() {}
    }

    private record Sample2(String name, int value) {}
}
