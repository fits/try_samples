package sample;

import java.util.Map;
import java.util.regex.Pattern;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.*;

@Aspect
public class StrutsAspect {
	private final static Pattern PATTERN = Pattern.compile("(^|\\W)[cC]lass\\W");

	@Around("call(void org.apache..BeanUtils.populate(Object, Map)) && within(org.apache.struts..*) && !within(org.apache.struts.mock.*) && args(bean, properties)")
	public void aroundPopulate(ProceedingJoinPoint pjp, Object bean, Map properties) {
		if (properties != null) {
			checkProperties(properties);

			pjp.proceed();
		}
	}

	private void checkProperties(Map properties) {
		for (Object key : properties.keySet()) {
			String property = (String)key;

			if (PATTERN.matcher(property).find()) {
				throw new IllegalArgumentException(key + " is invalid");
			}
		}
	}
}
