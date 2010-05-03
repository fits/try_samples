package test;

import org.seasar.framework.container.*;
import org.seasar.framework.container.factory.*;

public class Tester {

	private final static String PATH = "test.dicon";

	public static void main(String[] args) {

		S2Container container = S2ContainerFactory.create(PATH);
		container.init();

		try {

			printOut((Product)container.getComponent("product1"));

		} finally {
			container.destroy();
		}
	}

	private static void printOut(Product product) {
		System.out.println("----- Product -----");
		product.printOut();
		System.out.println("");
	}
}