package sample;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class App {
    public static void main(String... args) {
        var f = Instant.now();

        downloadToZip(Paths.get("output/output.zip"));

        var t = Instant.now();

        System.out.println("time=" + Duration.between(f, t).toMillis() + "ms");
    }

    private static void downloadToZip(Path zipFile) {
        mkdirs(zipFile);

        var client = HttpClient.newHttpClient();
        var env = Map.of("create", "true");

        try (var zipFs = FileSystems.newFileSystem(zipFile, env, null)) {

            var sc = new Scanner(System.in);
            var futures = new ArrayList<CompletableFuture<?>>();

            while(sc.hasNext()) {
                var url = URI.create(sc.nextLine());
                var req = HttpRequest.newBuilder(url).build();

                var future = client.sendAsync(
                        req,
                        HttpResponse.BodyHandlers.ofFile(zipFs.getPath(url.getPath()))
                );

                futures.add(future);
            }

            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void mkdirs(Path zipFile) {
        try {
            Files.createDirectories(zipFile.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
