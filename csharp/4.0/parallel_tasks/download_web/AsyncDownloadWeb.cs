
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Threading.Tasks;

public class AsyncDownloadWeb
{
	public static void Main(string[] args)
	{
		var urls = Console.In.ReadToEnd().Split(new string[]{Environment.NewLine}, StringSplitOptions.RemoveEmptyEntries);

		var dir = args[0];
		var list = new List<Task<bool>>();

		foreach (var u in urls)
		{
			var wc = new WebClient();
			var uri = new Uri(u);
			var fileName = Path.Combine(dir, Path.GetFileName(u));

			TaskCompletionSource<bool> tcs = new TaskCompletionSource<bool>();

			wc.DownloadFileCompleted += (sender, e) =>
			{
				if (e.Error != null)
				{
					Console.WriteLine("failed: {0}, {1}", uri, e.Error.Message);
					tcs.TrySetResult(false);
				}
				else
				{
					Console.WriteLine("downloaded: {0} => {1}", uri, fileName);
					tcs.TrySetResult(true);
				}
			};

			wc.DownloadFileAsync(uri, fileName);

			list.Add(tcs.Task);
		}

		Task.WaitAll(list.ToArray());
	}
}

