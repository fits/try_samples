
import akka.actor.ActorSystem;

import akka.japi.pf.Match;
import akka.japi.pf.PFBuilder;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;

import akka.util.ByteString;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class SampleApp {
    public static void main(String... args) throws IOException {
        final ActorSystem system = ActorSystem.create();
        final Materializer materializer = ActorMaterializer.create(system);

        CompletableFuture<?> res1 = Source.single("sample data")
                .map(ByteString::fromString)
                .runWith(FileIO.toPath(Paths.get("sample1.txt")), materializer)
                .toCompletableFuture();

        PFBuilder<Throwable, String> pfunc = 
            Match.match(IOException.class, e -> "no file");

        CompletableFuture<?> res2 = FileIO.fromPath(Paths.get("sample2.txt"))
                .map(ByteString::utf8String)
                .recover(pfunc.build())
                .runForeach(System.out::println, materializer)
                .toCompletableFuture();

        CompletableFuture.allOf(res1, res2)
                .handle((v, e) -> system.terminate())
                .join();
    }
}