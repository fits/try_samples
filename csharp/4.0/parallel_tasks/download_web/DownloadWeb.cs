
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
			try {
				new WebClient().DownloadFile(u, Path.Combine(dir, Path.GetFileName(u)));
				Console.WriteLine("success: {0}", u);
			}
			catch (Exception ex) {
				Console.WriteLine("failed: {0}, {1}", u, ex);
			}
		});
	}
}

