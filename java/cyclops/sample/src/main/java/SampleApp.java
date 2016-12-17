
import com.aol.cyclops.control.AnyM;
import com.aol.cyclops.types.anyM.AnyMValue;

import java.util.Optional;

public class SampleApp {
    public static void main(String... args) {

        AnyMValue<Integer> m1 = AnyM.fromOptional(Optional.of(123));
        m1.map(i -> i * 2).forEach(System.out::println);

        m1.flatMap(i -> m1.unit(i * 3)).forEach(System.out::println);

        System.out.println("------");

        AnyMValue<Integer> m2 = AnyM.fromOptional(Optional.empty());
        m2.map(i -> i * 2).forEach(System.out::println);
    }
}
