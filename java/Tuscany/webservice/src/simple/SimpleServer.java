package simple;

import java.io.*;
import org.apache.tuscany.sca.host.embedded.SCADomain;

public class SimpleServer {

    public static void main(String[] args) {
        
        SCADomain scad = SCADomain.newInstance("simple.composite");

        try {
            System.out.println("server started");
            System.in.read();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        scad.close();
    }
}
