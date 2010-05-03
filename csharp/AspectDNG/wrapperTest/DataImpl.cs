
using System;

public class DataImpl : IData
{
	public void CheckAudio(string bstrWMX, out int plPeakMin, out int plPeakMax)
	{
		plPeakMin = 10;
		plPeakMax = 100;

		Console.WriteLine("DataImpl.CheckAudio({0}, {1}, {2})", bstrWMX, plPeakMin, plPeakMax);
	}

	public void Print()
	{
		Console.WriteLine("DataImpl.Print()");
	}

	public string Test(string data, int i)
	{
		Console.WriteLine("DataImpl.Test({0}, {1})", data, i);
		return "DataImpl";
	}
}