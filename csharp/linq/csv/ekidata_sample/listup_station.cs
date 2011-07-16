using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using System.Text;

class ListUpStation
{
	public static void Main(string[] args)
	{
		var lines = File.ReadAllLines("m_station.csv", Encoding.Default);

		var q = (
			from l in lines.Skip(1)
			let s = l.Split(',')
			group s by new { 
				StationName = s[9],
				PrefName = s[10], 
				StationGroupCode = s[5]
			} into stGroup
			orderby stGroup.Count() descending
			select stGroup
		).Take(10);

		foreach(var l in q)
		{
			Console.WriteLine("{0}‰w ({1}) : {2}", l.Key.StationName, l.Key.PrefName, l.Count());
		}
	}
}