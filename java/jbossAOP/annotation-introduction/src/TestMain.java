
import java.lang.annotation.*;

public class TestMain {

    public static void main(String[] args) {

        for (Annotation an : TestData.class.getAnnotations()) {
            System.out.println(an);
        }

    }
}