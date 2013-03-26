package fits.sample;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.*;

import org.apache.ibatis.cache.Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class MyBatisCacheAspect {
	private static Logger log = LoggerFactory.getLogger("org.apache.ibatis");

	@Before(
		"execution(public void org..ibatis..Cache.putObject(Object, Object)) && args(key, value) && this(obj)"
	)
	public void beforePutObject(JoinPoint pjp, Object key, Object value, Cache obj) {
		log.info("*** " + "id=" + obj.getId() + ", size=" + obj.getSize() + ", key=" + key + ", value=" + value);
	}
}
