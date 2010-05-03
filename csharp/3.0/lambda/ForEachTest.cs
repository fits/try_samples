using System;
using System.Collections.Generic;

public class ForEachTest
{
	public static void Main(string[] args)
	{
		var items = new List<string>(){"test1", "aaaa", "1234", "チェック"};

		items.ForEach((item) =>
		{
			Console.WriteLine("value: {0}", item);
		});
	}
}