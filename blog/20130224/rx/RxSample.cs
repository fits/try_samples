
using System;
using System.Linq;
using System.Reactive.Linq;

class RxSample
{
	static void Main(string[] args)
	{
		Observable.Range(1, 3).Subscribe(Console.WriteLine);

		Console.WriteLine("-----");

		Observable.Range(1, 4).Where(x => x % 2 == 0).Select(x => "#" + x).Subscribe(Console.WriteLine);

		// ˆÈ‰º‚Å‚à‰Â
		//(from x in Observable.Range(1, 3) where x % 2 == 0 select "#" + x).Subscribe(Console.WriteLine);
	}
}
