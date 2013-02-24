
using System;
using System.IO;
using System.Linq;
using System.Reactive.Disposables;
using System.Reactive.Linq;

// Mono 3.0.3 ‚Å‚Í³í“®ì‚µ‚È‚¢ i.NET Framework 4.5 ‚Å‚Í–â‘è‚È‚µj
class RxAsyncFileSample
{
	static void Main(string[] args)
	{
		FromAsyncFile(args[0]).Subscribe(Console.WriteLine);

		Console.WriteLine("-----");

		FromAsyncFile(args[0]).Skip(1).Take(2).Select(x => "#" + x).Subscribe(Console.WriteLine);
	}

	private static IObservable<string> FromAsyncFile(string fileName)
	{
		return Observable.Create<string>(async observer => {
			try
			{
				using(var reader = File.OpenText(fileName))
				{
					while (!reader.EndOfStream)
					{
						var line = await reader.ReadLineAsync();
						observer.OnNext(line);
					}

					observer.OnCompleted();
					Console.WriteLine("*** close");
				}
			}
			catch (Exception error) {
				observer.OnError(error);
			}
			return Disposable.Empty;
		});
	}
}
