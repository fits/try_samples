using System;
using System.Linq;

public class SimpleObjectListup
{
	public static void Main(string[] args)
	{
		int[] list = {1, 5, 10, 30, 13, 75, 190, 137, 8};

		//‹ô”‚¾‚¯‚ð‘I‘ð
		var query = from d in list where d % 2 == 0 orderby d select d;

		foreach (var d in query)
		{
			Console.WriteLine(d);
		}

		Console.WriteLine("-----------");

		//2æ‚µ‚Ä 500 ‚æ‚è‘å‚«‚¢‚à‚Ì‚ð‘I‘ð
		var query2 = from d in list select d * d into d2 where d2 > 500 select d2;

		foreach (var d2 in query2)
		{
			Console.WriteLine(d2);
		}
	}
}