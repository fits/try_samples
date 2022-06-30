package sample;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public class App {
    public static void main(String... args) throws Exception {
        var f = Instant.now();

        downloadFile(args[0], Path.of(args[1])).get();

        var t = Instant.now();

        System.out.println("time=" + Duration.between(f, t).toMillis() + "ms");
    }

    private static CompletableFuture<?> downloadFile(String url, Path file) {
        var client = HttpClient.newHttpClient();
        var req = HttpRequest.newBuilder(URI.create(url)).build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofFile(file));
    }
}
