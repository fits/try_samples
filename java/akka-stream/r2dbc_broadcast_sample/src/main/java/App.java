
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ClosedShape;
import akka.stream.javadsl.*;
import akka.util.ByteString;

import io.r2dbc.spi.*;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class App {
    public static void main(String... args) {
        final var uri = Optional.ofNullable(System.getenv("DB_URI"))
                .orElse("r2dbc:mariadb://user:password@localhost/sample");

        final var system = ActorSystem.create();
        final var factory = ConnectionFactories.get(uri);

        var source = findTasks(factory);

        var todoSink = filterByStatus("ready")
                .map(App::toByteString)
                .toMat(FileIO.toPath(Paths.get("todo.txt")), Keep.right());

        var doneSink = filterByStatus("completed")
                .map(App::toByteString)
                .toMat(FileIO.toPath(Paths.get("done.txt")), Keep.right());

        var graph = GraphDSL.create(
                List.of(todoSink, doneSink),
                (b, outs) -> {
                    var broadcast = b.add(Broadcast.<Task>create(outs.size()));

                    b.from(b.add(source)).viaFanOut(broadcast);

                    outs.forEach(sink -> b.from(broadcast).to(sink));

                    return ClosedShape.getInstance();
                }
        );

        var res = RunnableGraph.fromGraph(graph).run(system);

        CompletableFuture.allOf(res.toArray(CompletableFuture[]::new))
                .exceptionally(err -> {
                    err.printStackTrace();
                    return null;
                })
                .thenRun(() -> system.terminate());
    }

    private static ByteString toByteString(Task task) {
        var s = String.join(",", List.of(Long.toString(task.id()), task.subject()));
        return ByteString.fromString(s + System.lineSeparator());
    }

    private static Flow<Task, Task, NotUsed> filterByStatus(String status) {
        return Flow.of(Task.class).filter(t -> t.status().equalsIgnoreCase(status));
    }

    private static Source<Task, NotUsed> findTasks(ConnectionFactory factory) {
        var sql = "SELECT * FROM tasks";

        return Source.fromPublisher(factory.create())
                .map(con -> con.createStatement(sql))
                .flatMapConcat(st -> Source.fromPublisher(st.execute()))
                .flatMapConcat(rs -> Source.fromPublisher(rs.map(App::toTask)));
    }

    private static Task toTask(Row r, RowMetadata m) {
        return new Task(
                r.get("id", long.class),
                r.get("subject", String.class),
                r.get("status", String.class)
        );
    }

    private record Task(long id, String subject, String status) {}
}