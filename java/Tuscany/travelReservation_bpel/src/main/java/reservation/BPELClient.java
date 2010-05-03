package reservation;

import org.apache.tuscany.sca.host.embedded.SCADomain;

public class BPELClient {
    public static void main(String[] args) throws Exception {

        SCADomain scaDomain = SCADomain.newInstance("TravelReservation.composite");

		System.out.println("start travelReservation ...");

		System.in.read();

        scaDomain.close();

		System.out.println("stop ...");

        System.exit(0);
    }
}
