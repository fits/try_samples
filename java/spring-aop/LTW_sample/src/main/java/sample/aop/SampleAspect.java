package sample.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class SampleAspect {
    @Before("execution(* org.springframework.beans.factory..*(..))")
    public void dump(JoinPoint jp) {
        System.out.println("*** before: " + jp.toShortString());
    }
}
