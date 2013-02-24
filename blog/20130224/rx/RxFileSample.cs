
using System;
using System.IO;
using System.Linq;
using System.Reactive.Disposables;
using System.Reactive.Linq;

class RxFileSample
{
	static void Main(string[] args)
	{
		FromFile(args[0]).Subscribe(Console.WriteLine);

		Console.WriteLine("-----");

		FromFile(args[0]).Skip(1).Take(2).Select(x => "#" + x).Subscribe(Console.WriteLine);
	}

	private static IObservable<string> FromFile(string fileName)
	{
		return Observable.Create<string>(observer => {
			try
			{
				using(var reader = File.OpenText(fileName))
				{
					while (!reader.EndOfStream)
					{
						observer.OnNext(reader.ReadLine());
					}
				}
				observer.OnCompleted();
			}
			catch (Exception error) {
				observer.OnError(error);
			}
			return Disposable.Empty;
		});
	}
}
