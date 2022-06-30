package sample;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class App {
    public static void main(String... args) {
        var f = Instant.now();

        var size = downloadFile(args[0], Path.of(args[1])).block();

        var t = Instant.now();

        System.out.println("size=" + size + ", time=" + Duration.between(f, t).toMillis() + "ms");
    }

    private static Mono<AsynchronousFileChannel> createFile(Path file) {
        return Mono.create(sink -> {
            try {
                sink.success(AsynchronousFileChannel.open(file, CREATE, WRITE));
            } catch (IOException e) {
                sink.error(e);
            }
        });
    }

    private static void closeFile(AsynchronousFileChannel fc) {
        try {
            fc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Mono<Long> downloadFile(String url, Path file) {
        return createFile(file).flatMap(fc ->
                downloadFile(url, fc).doFinally(sg -> closeFile(fc))
        );
    }

    private static Mono<Long> downloadFile(String url, AsynchronousFileChannel fileChannel) {
        return Mono.create(sink -> HttpClient
                .create()
                .get()
                .uri(url)
                .responseContent()
                .asByteBuffer()
                .subscribe(new Subscriber<>() {
                    private final AtomicLong pos = new AtomicLong(0);
                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription s) {
                        System.out.println("*** onSubscribe");
                        subscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(ByteBuffer byteBuffer) {
                        System.out.println("*** onNext: " + byteBuffer);

                        fileChannel.write(byteBuffer, pos.get(), byteBuffer, new CompletionHandler<>() {
                            @Override
                            public void completed(Integer result, ByteBuffer attachment) {
                                pos.addAndGet(result);
                                subscription.request(1);
                            }

                            @Override
                            public void failed(Throwable exc, ByteBuffer attachment) {
                                onError(exc);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println("*** onError: " + t);
                        subscription.cancel();
                        sink.error(t);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("*** onComplete");
                        sink.success(pos.get());
                    }
                }));
    }
}
