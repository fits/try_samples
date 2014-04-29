package fits.sample;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.*;

@Aspect
public class StrutsAspect {
	@Before("call(void org.apache..BeanUtils.populate(..)) && (within(org.apache.struts..RequestUtils) || within(org.apache.struts..ActionServlet))")
	public void beforePopulate(JoinPoint jp) {
		System.out.println("***** " + jp);
	}
}
