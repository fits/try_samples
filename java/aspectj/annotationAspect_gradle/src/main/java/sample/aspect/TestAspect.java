package sample.aspect;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;

import sample.annotation.*;

@Aspect
public class TestAspect {

	private void printData(String kind, JoinPoint joinPoint) {
		System.out.println(kind + " : signature : " + joinPoint.getSignature() + ", this : " + joinPoint.getThis() + ", target : " + joinPoint.getTarget());
	}

	@After("call(@CheckPoint * *(..))")
	public void advice(JoinPoint jp) {
		this.printData("CALL", jp);
	}

}