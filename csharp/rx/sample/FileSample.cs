
using System;
using System.IO;
using System.Linq;
using System.Reactive.Disposables;
using System.Reactive.Linq;

class FileSample
{
	static void Main(string[] args)
	{
		FromFile(args[0]).Subscribe(Console.WriteLine);

		Console.WriteLine("-----");

		FromFile(args[0]).Skip(1).Take(2).Select(x => "#" + x).Subscribe(Console.WriteLine);

		Console.WriteLine("-----");

		// 下記は Mono 3.0.3 では動作しない （.NET Framework 4.5 では動作する）
		FromAsyncFile(args[0]).Skip(1).Take(2).Select(x => "#" + x).Subscribe(Console.WriteLine);
	}

	// 同期処理版
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

	// 非同期処理版 （Mono 3.0.3 では正常に動作しない）
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
