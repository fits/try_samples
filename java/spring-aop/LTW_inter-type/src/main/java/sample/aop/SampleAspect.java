package sample.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareParents;

@Aspect
public class SampleAspect {
    public interface Show {
        default String show() {
            return "show( " + this.toString() + " )";
        }
    }

    @DeclareParents("sample.*.*")
    private Show implementedInterface;

    @Before("execution(* sample.*.*.*(..)) && this(s)")
    public void dump(JoinPoint jp, Show s) {
        System.out.println("*** before: " + s.show() + ", " + jp.toShortString());
    }
}
