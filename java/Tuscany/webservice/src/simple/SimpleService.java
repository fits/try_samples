package simple;

import org.osoa.sca.annotations.Remotable;
import org.osoa.sca.annotations.Service;

@Remotable
@Service
public interface SimpleService {

    String getGreetings(String name);

    int calculate(int a, int b);

}
