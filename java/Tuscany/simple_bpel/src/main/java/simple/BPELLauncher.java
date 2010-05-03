package simple;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class BPELLauncher {
    public static void main(String[] args) throws Exception {

        SCADomain scaDomain = SCADomain.newInstance("SimpleTest.composite");

		System.out.println("start SimpleTest ...");

		System.in.read();

        scaDomain.close();

		System.out.println("stop ...");

        System.exit(0);
    }
}
