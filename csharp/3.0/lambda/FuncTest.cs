using System;

public class FuncTest
{
	public static void Main(string[] args)
	{
		Func<string, bool> method1 = (msg) =>
		{
			Console.WriteLine(msg + "_aaa");
			return true;
		};

		Console.WriteLine("result : {0}", method1("test"));
	}
}