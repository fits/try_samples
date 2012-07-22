using System;
using System.IO;

using MarkdownDeep;

class ToHtmlSample
{
	static void Main(string[] args)
	{
		string markdown = File.ReadAllText(args[0]);

		var md = new Markdown();

		string html = md.Transform(markdown);

		Console.WriteLine(html);
	}
}