using System;

public class Tester
{
	public static void Main(string[] args)
	{
		Source sc = new Source();
		sc.TestA("テストデータ");

		Console.WriteLine("return : {0}", sc.Check(100, "a"));

		sc.PrintLog();

		IChecker checker = sc as IChecker;

		if (checker != null)
		{
			Console.WriteLine("Interface Imple : {0}", checker.Check(100, "a"));
		}
	}
}
