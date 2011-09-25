
using System;
using System.IO;
using System.Net;
using System.Threading.Tasks;

public class DownloadWeb
{
	public static void Main(string[] args)
	{
		var urls = Console.In.ReadToEnd().Split(new string[]{Environment.NewLine}, StringSplitOptions.RemoveEmptyEntries);

		var dir = args[0];

		Parallel.ForEach(urls, (url) => {
			try {
				var filePath = Path.Combine(dir, Path.GetFileName(url));
				new WebClient().DownloadFile(url, filePath);

				Console.WriteLine("downloaded: {0} => {1}", url, filePath);
			}
			catch (Exception e) {
				Console.WriteLine("failed: {0}, {1}", url, e);
			}
		});
	}
}

