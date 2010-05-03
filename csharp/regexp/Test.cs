using System;
using System.Text.RegularExpressions;

public class Test
{
	public static void Main(string[] args)
	{
		string data = "test(a) + test(b) = c";

		Regex regexMax = new Regex("test\\((.*)\\)");
		Console.WriteLine("--- Å’·ˆê’v ---");
		PrintMatch(regexMax, data);

		Regex regexMin = new Regex("test\\((.*?)\\)");
		Console.WriteLine("--- Å’Zˆê’v ---");
		PrintMatch(regexMin, data);

	}

	private static void PrintMatch(Regex regex, string data)
	{
		foreach (Match m in regex.Matches(data))
		{
			Console.WriteLine("match : {0}, group1 : {1}", m.Value, m.Groups[1].Value);
		}
	}
}