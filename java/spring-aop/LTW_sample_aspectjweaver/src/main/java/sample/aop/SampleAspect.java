package sample.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class SampleAspect {
    @Before("execution(* sample.*.*.*(..))")
    public void dump(JoinPoint jp) {
        System.out.println("*** before: " + jp.toShortString());
    }

    @Around("execution(String sample..internal*(..)) && args(value)")
    public String replaceProcess(String value) {
        return "replaced:" + value;
    }
}
