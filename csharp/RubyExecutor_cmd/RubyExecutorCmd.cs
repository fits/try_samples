using System;
using System.Diagnostics;

public class RubyExecutorCmd
{
	public static void Main(string[] args)
	{
		ProcessStartInfo info = new ProcessStartInfo("ruby.exe", string.Join(" ", args));
		info.CreateNoWindow = true;
		info.RedirectStandardOutput = true;
		info.RedirectStandardError = true;
		info.UseShellExecute = false;

		Process p = Process.Start(info);

		Console.WriteLine(p.StandardOutput.ReadToEnd());
	}
}
