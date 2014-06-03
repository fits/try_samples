using System;
using System.Collections.Generic;
using System.Linq;

class GiniImpurity
{
	public static void Main(string[] args)
	{
		var list = new List<string>() {"A", "B", "B", "C", "B", "A"};

		Console.WriteLine("{0}", gini1(list));
		Console.WriteLine("{0}", gini2(list));
	}

	private static double gini1<K>(IEnumerable<K> xs)
	{
		 return 1 - xs.GroupBy(x => x).Select(x => Math.Pow((double)x.Count() / xs.Count(), 2)).Sum();
	}

	private static double gini2<K>(IEnumerable<K> xs)
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

	private static IEnumerable<Tuple<Tuple<K, V>, Tuple<K, V>>> combination<K, V>(IEnumerable<Tuple<K, V>> data) {

		return
			from x in data
			from y in data
			where !x.Item1.Equals(y.Item1)
			select Tuple.Create(x, y);
	}
} 