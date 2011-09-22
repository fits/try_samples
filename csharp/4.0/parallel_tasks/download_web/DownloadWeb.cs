
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

		Parallel.ForEach(urls, (u) => {
			var wc = new WebClient();
			wc.DownloadFile(u, Path.Combine(dir, Path.GetFileName(u)));
		});
	}
}

