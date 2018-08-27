
import org.jooby.Jooby;
import org.jooby.json.Jackson;

public class App extends Jooby {
    {
        use(new Jackson());

        get(req -> {
            var name = req.param("name").value("unknown");
            return "name:" + name;
        });

        get("/data/:id", req -> new Data(req.param("id").value()));
    }

    public static class Data {
        public String id;
        public int value = 5;

        public Data(String id) {
            this.id = id;
        }
    }

    public static void main(String... args) {
        run(App::new, args);
    }
}
