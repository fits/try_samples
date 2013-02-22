
using System;
using System.Linq;
using System.Reactive.Linq;

class Sample
{
	static void Main(string[] args)
	{
		var rx = from x in Enumerable.Range(1, 10) where x % 3 == 0 select x + 10;
		foreach (var r in rx)
		{
			Console.WriteLine(r);
		}
	}
}
