using System;
using System.Collections.Generic;
using System.Linq;

class Gini
{
	public static void Main(string[] args)
	{
		var list = new List<string>() {"A", "B", "B", "C", "B", "A"};

		Console.WriteLine("{0}", giniA(list));
		Console.WriteLine("{0}", giniB(list));
	}

	// (a) 1 - (AA + BB + CC)
	private static double giniA<K>(IEnumerable<K> xs)
	{
		 return 1 - xs.GroupBy(x => x).Select(x => Math.Pow((double)x.Count() / xs.Count(), 2)).Sum();
	}

	// (b) AB + AC + BA + BC + CA + CB
	private static double giniB<K>(IEnumerable<K> xs)
	{
		return
			combination(
				countBy(xs).Select(t =>
					Tuple.Create(t.Item1, (double)t.Item2 / xs.Count())
				)
			).Select(x => x.Item1.Item2 * x.Item2.Item2).Sum();
	}

	private static IEnumerable<Tuple<K, int>> countBy<K>(IEnumerable<K> xs) {
		return xs.GroupBy(x => x).Select(g => Tuple.Create(g.Key, g.Count()));
	}

	// 異なる要素の組み合わせを作成
	private static IEnumerable<Tuple<Tuple<K, V>, Tuple<K, V>>> combination<K, V>(IEnumerable<Tuple<K, V>> data) {

		return
			from x in data
			from y in data
			where !x.Item1.Equals(y.Item1)
			select Tuple.Create(x, y);
	}
} 