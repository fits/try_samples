package sample;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

public class SampleValve extends ValveBase {
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        printRequest("--- sampleValve before next invoke", request);

        getNext().invoke(request, response);

        printRequest("--- sampleValve after next invoke", request);
    }

    private void printRequest(String title, Request request) {
        System.out.println(title + ": uri=" + request.getRequestURI() + ", session=" + request.getSession(false));
    }
}
