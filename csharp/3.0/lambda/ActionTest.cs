using System;

public class ActionTest
{
	public static void Main(string[] args)
	{
		Action<string> method1 = (msg) => Console.WriteLine(msg + "_aaa");

		method1("test");
	}
}