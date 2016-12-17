
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.control.SimpleReact;
import com.aol.cyclops.data.collections.extensions.standard.ListX;

public class SampleApp {
    public static void main(String... args) {

        ListX.of("A")
                .cycle(3)
                .parallelStream()
                .map(m -> {
                    printThread(m + "_map");
                    return m;
                })
                .forEach(m -> printThread(m + "_foreach"));

        System.out.println("------");

        AnyM.fromArray("B")
                .cycle(3)
                .toList()
                .parallelStream()
                .map(m -> {
                    printThread(m + "_map");
                    return m;
                })
                .forEach(m -> printThread(m + "_foreach"));

        System.out.println("------");

        SimpleReact.parallelBuilder().of("C", "C", "C").then(m -> {
            try {
                Thread.currentThread().sleep(2000);
            } catch(Exception ex) {}

            printThread(m + "_then");

            return null;
        }).block();


    }

    private static void printThread(String msg) {
        System.out.println("message = " + msg + ", thread = " + Thread.currentThread());
    }
}
