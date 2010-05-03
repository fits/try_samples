
using System;

public class DataWrapper : IData
{
	public void CheckAudio(string bstrWMX, out int plPeakMin, out int plPeakMax)
	{
		plPeakMin = 1;
		plPeakMax = 2;

		Console.WriteLine("DataWrapper.CheckAudio({0}, {1}, {2})", bstrWMX, plPeakMin, plPeakMax);
	}

	public void Print()
	{
		Console.WriteLine("DataWrapper.Print()");
	}

	public string Test(string data, int i)
	{
		Console.WriteLine("DataWrapper.Test({0}, {1})", data, i);
		return "DataWrapper";
	}
}