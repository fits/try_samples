using System;
using System.Linq;
using System.IO;
using System.Text;

class ListUpStation
{
	public static void Main(string[] args)
	{
		var plines = File.ReadAllLines("m_pref.csv", Encoding.Default);

		var prefs = 
			from pline in plines.Skip(1)
				let p = pline.Split(',')
			select new {
				PrefCode = p[0],
				PrefName = p[1]
			};

		var slines = File.ReadAllLines("m_station.csv", Encoding.Default);

		var list = (
			from sline in slines.Skip(1)
				let s = sline.Split(',')
			join p in prefs on s[10] equals p.PrefCode
			group s by new { 
				StationName = s[9],
				PrefName = p.PrefName, 
				StationGroupCode = s[5]
			} into stGroup
			orderby stGroup.Count() descending
			select stGroup
		).Take(10);

		foreach(var s in list)
		{
			Console.WriteLine("{0}‰w ({1}) : {2}", s.Key.StationName, s.Key.PrefName, s.Count());
		}
	}
}