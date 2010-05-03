
import org.jboss.aop.Advised;
import org.jboss.aop.AspectManager;
import org.jboss.aop.advice.AdviceBinding;
import org.jboss.aop.pointcut.ast.ParseException;

public class TestMain {

    private static String adviceName;

    public static void main(String[] args) {

        TestData<String, Integer> data = new TestData<String, Integer>();
        data.addData("k1", 5);
        printTestData(data);

        System.out.println("-----------------------------");

        TestData<Integer, Integer> data2 = new TestData<Integer, Integer>();
        ((Advised)data2)._getInstanceAdvisor().insertInterceptor(new SimpleInterceptor());
        data2.addData(100, 200);
        printTestData(data2);

        System.out.println("-----------------------------");

        prepareAspect();

        TestData<Integer, Integer> data3 = new TestData<Integer, Integer>();
        data3.addData(1, 2);

        printTestData(data3);

        System.out.println("-----------------------------");

        data.addData("", 1);
        printTestData(data);

        System.out.println("-----------------------------");

        data2.addData(1, 2);
        printTestData(data2);

        System.out.println("-----------------------------");

        clearAspect();

        data.addData("aaa", 10000);
        printTestData(data);
    }

    private static void printTestData(TestData data) {
        System.out.printf("*** data: %s ***\n", data);
        data.printData();
    }

    private static void prepareAspect() {
        try {
            AdviceBinding binding = new AdviceBinding("execution(* *->addData(..))", null);
            binding.addInterceptor(TestCallerInterceptor1.class);
            AspectManager.instance().addBinding(binding);

            adviceName = binding.getName();

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private static void clearAspect() {
        AspectManager.instance().removeBinding(adviceName);
    }

}