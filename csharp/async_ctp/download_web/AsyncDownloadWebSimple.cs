
using System;
using System.IO;
using System.Net;
using System.Threading.Tasks;

public class AsyncDownloadWebSimple
{
	public static void Main(string[] args)
	{
		var urls = Console.In.ReadToEnd().Split(new string[]{Environment.NewLine}, StringSplitOptions.RemoveEmptyEntries);

		var dir = args[0];
		var task = DownloadAll(dir, urls);

		Task.WaitAll(task);
	}

	private static async Task DownloadAll(string dir, string[] urls)
	{
		foreach (var u in urls)
		{
			try {
				await Download(dir, u);
				Console.WriteLine("download: {0}", u);
			} catch (Exception ex) {
				Console.WriteLine("failed: {0}, {1}", u, ex.Message);
			}
		}
	}

	private static async Task Download(string dir, string url)
	{
		var wc = new WebClient();
		var uri = new Uri(url);
		var fileName = Path.Combine(dir, Path.GetFileName(url));

		await wc.DownloadFileTaskAsync(uri, fileName);
	}
}

