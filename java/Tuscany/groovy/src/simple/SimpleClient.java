package simple;

import java.io.*;
import org.apache.tuscany.sca.host.embedded.SCADomain;

public class SimpleClient {

    public static void main(String[] args) {

        SCADomain scad = SCADomain.newInstance("simple.composite");

        SimpleService service = scad.getService(SimpleService.class, "SimpleServiceComponent");

        System.out.printf("result: %s\n", service.getGreetings("test"));
        System.out.printf("%d + %d = %d\n", 5, 10, service.calculate(5, 10));


        scad.close();
    }
}
