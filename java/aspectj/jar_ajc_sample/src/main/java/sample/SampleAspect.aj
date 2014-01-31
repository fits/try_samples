package sample;

import java.util.*;

import org.aspectj.lang.annotation.*;
import org.aspectj.lang.*;

privileged aspect SampleAspect {

	 Object around(Map header, List lines) : call(* fits..SampleServiceImpl.sample(..)) && args(header, lines) {

		System.out.println("*** call sample()");

		return proceed(header, lines);
	 }
}
