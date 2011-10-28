
using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Threading.Tasks;

public class AsyncDownloadWebSimple
{
	public static void Main(string[] args)
	{
		var urls = Console.In.ReadToEnd().Split(new string[]{Environment.NewLine}, StringSplitOptions.RemoveEmptyEntries);

		var dir = args[0];
		var list = new List<Task>(urls.Length);

		foreach (var u in urls)
		{
			list.Add(Download(dir, u));
		}

		Task.WaitAll(list.ToArray());
	}

	private static async Task Download(string dir, string url)
	{
		var wc = new WebClient();
		var uri = new Uri(url);
		var fileName = Path.Combine(dir, Path.GetFileName(url));

		try {
			await wc.DownloadFileTaskAsync(uri, fileName);

			Console.WriteLine("download: {0} => {1}", url, fileName);
		} catch (Exception ex) {
			Console.WriteLine("failed: {0}, {1}", url, ex.Message);
		}
	}
}

