import javax.jws.WebService
import javax.jws.WebMethod

@WebService(targetNamespace = "http://test")
class TestMessage {

    @WebMethod
    def String hello(String msg) {
        return "${msg}:${this}"
    }
}
