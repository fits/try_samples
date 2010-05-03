
import javax.jws.WebService;

@WebService(targetNamespace = "http://test")
public class TestMessage {

    public String getMessage() {
        return "ƒeƒXƒg:" + this;
    }

}