using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;

class ListUpStation
{
	public static void Main(string[] args)
	{
		var lines = new List<string>(File.ReadAllLines("m_station.csv", Encoding.Default));
		lines.RemoveAt(0);

		var q = 
			from l in lines
			let s = l.Split(',')
			group s by new { 
				StationName = s[9],
				PrefName = s[10], 
				stationGroupCode = s[5]
			};

		foreach(var l in q)
		{
			Console.WriteLine(l);
		}
	}
}