package simple;

import org.osoa.sca.annotations.Service;

//@Service(SimpleService.class)
public class SimpleServiceImpl implements SimpleService {

    public String getGreetings(String name) {
        return name + this;
    }

    public int calculate(int a, int b) {
        return a + b;
    }
}
