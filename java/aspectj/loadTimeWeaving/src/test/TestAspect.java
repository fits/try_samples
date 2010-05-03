package test;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

@Aspect
public class TestAspect {

	private void printData(String kind, JoinPoint joinPoint) {
		System.out.println(kind + " : signature : " + joinPoint.getSignature() + ", this : " + joinPoint.getThis() + ", target : " + joinPoint.getTarget());
	}

	@After("call(@test.CheckPoint * *.*())")
	public void adviceCheck(JoinPoint jp) {
		this.printData("CheckPoint CALL", jp);
	}

	@After("call(* test.Data.*(..))")
	public void advice(JoinPoint jp) {
		this.printData("CALL", jp);
	}
}