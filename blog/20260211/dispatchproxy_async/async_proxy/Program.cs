
var proxy = AsyncProxy<ICheckService>.CreateProxy(
    new CheckService(),
    async (x) =>
    {
        var (_, a) = x;

        Console.WriteLine($"* before start: {string.Join(",", a ?? [])}");
        await Task.Delay(100);
        Console.WriteLine($"* before end: {string.Join(",", a ?? [])}");
    },
    async (x) => 
    {
        var (_, a) = x;

        Console.WriteLine($"* after start: {string.Join(",", a ?? [])}");
        await Task.Delay(100);
        Console.WriteLine($"* after end: {string.Join(",", a ?? [])}");
    }
);

proxy.FirstCheck("a1");
Console.WriteLine("-----");

await proxy.SecondCheckAsync("b2", 2);
Console.WriteLine("-----");

var res = await proxy.ThirdCheckAsync("c3", 3);
Console.WriteLine(res);
Console.WriteLine("-----");

try
{
    await proxy.FourthCheckAsync("d4", false);
}
catch (Exception ex)
{
    Console.WriteLine($"** ERROR: {ex.Message}");
}
Console.WriteLine("-----");
try
{
    var _ = await proxy.FifthCheckAsync("e5", false);
}
catch (Exception ex)
{
    Console.WriteLine($"** ERROR: {ex.Message}");
}

interface ICheckService
{
    int FirstCheck(string id);
    Task SecondCheckAsync(string id, int value);
    Task<string> ThirdCheckAsync(string id, int value);

    Task FourthCheckAsync(string id, bool value);
    Task<string> FifthCheckAsync(string id, bool value);
}

class CheckService : ICheckService
{
    public int FirstCheck(string id)
    {
        Console.WriteLine($"FirstCheck: {id}");
        return id.Length;
    }
    
    public async Task SecondCheckAsync(string id, int value)
    {
        Console.WriteLine($"SecondCheckAsync start: {id}, {value}");

        await Task.Delay(50);

        Console.WriteLine($"SecondCheckAsync end: {id}, {value}");
    }

    public async Task<string> ThirdCheckAsync(string id, int value)
    {
        Console.WriteLine($"ThirdCheckAsync start: {id}, {value}");

        await Task.Delay(50);
        var res = $"ok-{id}-{value}";

        Console.WriteLine($"ThirdCheckAsync end: {id}, {value}");

        return res;
    }

    public async Task FourthCheckAsync(string id, bool value)
    {
        Console.WriteLine($"FourthCheckAsync start: {id}, {value}");

        await Task.Delay(50);

        if (!value)
        {
            throw new Exception("FourthCheckAsync Error");
        }

        Console.WriteLine($"FourthCheckAsync end: {id}, {value}");
    }

    public async Task<string> FifthCheckAsync(string id, bool value)
    {
        Console.WriteLine($"FifthCheckAsync start: {id}, {value}");

        await Task.Delay(50);

        if (!value)
        {
            throw new Exception("FifthCheckAsync Error");
        }

        Console.WriteLine($"FifthCheckAsync end: {id}, {value}");

        return "ok";
    }
}
