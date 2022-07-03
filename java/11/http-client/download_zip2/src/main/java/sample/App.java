package sample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class App {
    private static final int BUF_SIZE = 1024;

    public static void main(String... args) {
        var f = Instant.now();

        downloadToZip(Paths.get("output/output.zip"));

        var t = Instant.now();

        System.out.println("time=" + Duration.between(f, t).toMillis() + "ms");
    }

    private static void downloadToZip(Path zipFile) {
        var sc = new Scanner(System.in);
        var client = HttpClient.newHttpClient();

        var futures = new ArrayList<CompletableFuture<Download>>();

        while(sc.hasNext()) {
            var url = URI.create(sc.nextLine());
            var req = HttpRequest.newBuilder(url).build();

            var future = client.sendAsync(
                    req,
                    HttpResponse.BodyHandlers.ofInputStream()
            ).thenApplyAsync(res -> new Download(url, res.body()));

            futures.add(future);
        }

        if (!futures.isEmpty()) {
            try (var zos = createZipStream(zipFile)) {
                var buf = new byte[BUF_SIZE];
                int len;

                for (var f : futures) {
                    var d = f.get();
                    var fileName = d.url.getPath().replaceAll("/", "");

                    zos.putNextEntry(new ZipEntry(fileName));

                    try (var input = new BufferedInputStream(d.responseBody)) {
                        while ((len = input.read(buf)) > -1) {
                            zos.write(buf, 0, len);
                        }
                    }
                    zos.closeEntry();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static ZipOutputStream createZipStream(Path zipFile) throws IOException {
        Files.createDirectories(zipFile.getParent());
        return new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipFile)));
    }

    private record Download(URI url, InputStream responseBody) {}
}
