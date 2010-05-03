
import test.TestMessage;
import test.TestMessageService;

public class TestClient {

    public static void main(String[] args) {
    
        TestMessageService service = new TestMessageService();
        TestMessage tm = service.getTestMessagePort();

        System.out.println(tm.getMessage());
    }
}
