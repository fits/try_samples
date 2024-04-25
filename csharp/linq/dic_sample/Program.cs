var ds1 = new Dictionary<string, int>
{
    {"a", 1},
    {"b", 2},
    {"c", 3},
    {"d", 4},
    {"e", 5},
    {"f", 6}
};

var ds2 = ds1
    .Where(p => p.Value % 2 == 1)
    .Select(p => new { p.Key, Value = p.Value.ToString() })
    .ToDictionary(p => p.Key, p => p.Value);

Console.WriteLine(ds2);

foreach (var d in ds2)
{
    Console.WriteLine(d);
}
