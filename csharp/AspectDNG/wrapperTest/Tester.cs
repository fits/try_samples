
using System;

public class Tester
{
	public static void Main(string[] args)
	{
		IData data = new DataWrapper();

		data.Print();

		int min, max;

		//このメソッドはウェービングの対象にならない
		data.CheckAudio("testbstr", out min, out max);

		Console.WriteLine("min={0}, max={1}", min, max);

		Console.WriteLine(data.Test("checkdata", 500));
	}
}
