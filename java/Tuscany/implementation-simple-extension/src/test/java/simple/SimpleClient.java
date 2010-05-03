package simple;

//import java.io.*;
import org.apache.tuscany.sca.host.embedded.SCADomain;

public class SimpleClient {

    public static void main(String[] args) {

        SCADomain scad = SCADomain.newInstance("simple.composite");

        Simple service = scad.getService(Simple.class, "SimpleServiceComponent");

        System.out.printf("result: %s\n", service.getGreetings("a1234"));

        scad.close();
    }
}
