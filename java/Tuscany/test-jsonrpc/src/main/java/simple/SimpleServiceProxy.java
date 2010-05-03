package simple;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

@Service(SimpleService.class)
public class SimpleServiceProxy implements SimpleService {

	private SimpleService simpleService;

	@Reference
	public void setSimpleService(SimpleService simpleService) {
		this.simpleService = simpleService;
	}

	public int calculate(int a, int b) {
		System.out.println("call calculate : " + this.simpleService);
	
	
		return this.simpleService.calculate(a, b);
	}
}
