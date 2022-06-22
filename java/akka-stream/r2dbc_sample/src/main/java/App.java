
import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.javadsl.Source;

import io.r2dbc.spi.*;

import java.util.Optional;

public class App {
    public static void main(String... args) {
        final var uri = Optional.ofNullable(System.getenv("DB_URI"))
                .orElse("r2dbc:mariadb://user:password@localhost/sample");

        final var sql = "SELECT * FROM tasks WHERE status = ?";

        final var system = ActorSystem.create();

        var factory = ConnectionFactories.get(uri);

        var res = Source.fromPublisher(factory.create())
                .map(con -> con.createStatement(sql).bind(0, "ready"))
                .flatMapConcat(st -> Source.fromPublisher(st.execute()))
                .flatMapConcat(rs -> Source.fromPublisher(rs.map(App::toTask)))
                .runForeach(System.out::println, system);

        res.exceptionally(App::printError).thenRun(() -> system.terminate());
    }

    private static Task toTask(Row r, RowMetadata m) {
        return new Task(
                r.get("id", long.class),
                r.get("subject", String.class),
                r.get("status", String.class)
        );
    }

    private static Done printError(Throwable err) {
        err.printStackTrace();
        return Done.done();
    }

    private record Task(long id, String subject, String status) {}
}