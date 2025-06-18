using System.Text.Json;
using System.ComponentModel.DataAnnotations;

var s = """
{
    "Id": "A01",
    "Date": "2025-07-01T10:00:00+09:00"
}
""";

var a = JsonSerializer.Deserialize<A>(s);
Console.WriteLine($"{a}");

var b = JsonSerializer.Deserialize<B>(s);
b?.Show();

try
{
    var c = JsonSerializer.Deserialize<C>(s);
    c?.Show();
} catch(Exception ex)
{
    Console.WriteLine($"Deserialize Failed : {ex}");
}

record A(string Code, int Value);

class B
{
    [Required]
    public string Code { get; set; } = string.Empty;

    public int Value { get; set; }

    public void Show()
    {
        Console.WriteLine($"code={Code}, value={Value}");
    }
}

class C
{
    public required string Code { get; set; } = string.Empty;

    public int Value { get; set; }

    public void Show()
    {
        Console.WriteLine($"code={Code}, value={Value}");
    }
}
