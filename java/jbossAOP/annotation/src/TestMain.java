
import org.jboss.aop.Advised;
import org.jboss.aop.AspectManager;
import org.jboss.aop.advice.AdviceBinding;
import org.jboss.aop.pointcut.ast.ParseException;

public class TestMain {

    public static void main(String[] args) {

        TestData<String, Integer> data = new TestData<String, Integer>();
        data.addData("k1", 5);
        data.printData();
    }

}