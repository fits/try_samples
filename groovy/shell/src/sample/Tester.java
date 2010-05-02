
package sample;

import groovy.lang.GroovyShell;

public class Tester {

	public static void main(String[] args) throws Exception {

		String script = "def msg = \"‚Ä‚·‚Æ\"\n println \"message : ${msg}\"";

		GroovyShell shell = new GroovyShell();
		Object result = shell.run(script, "test.groovy", (String[])null);

		System.out.printf("result : %s", result);
	}

}