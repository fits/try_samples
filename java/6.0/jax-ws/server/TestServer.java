import javax.xml.ws.Endpoint;

public class TestServer {

    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8080/test", new TestMessage());
    }
}
