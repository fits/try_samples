using System;
using System.Collections.Generic;

public class Test
{
	public static void Main(string[] args)
	{
		List<string> list = new List<string>();
		list.Add("‚Ä‚·‚Æ");
		list.Add("1");
		list.Add("abc");

		list.ForEach(delegate(string item)
		{
			Console.WriteLine("item: {0}", item);
		});
	}

}