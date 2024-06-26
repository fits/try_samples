using MessagePack;
using System.Diagnostics;

var d1 = new Cart(
    "c1", 
    new List<CartLine>{ new ("item-1", 1), new ("item-2", 2), new ("item-3", 3) }
);

PrintCart(d1);

var sw = new Stopwatch();
sw.Start();

var buf = MessagePackSerializer.Serialize(d1);

sw.Stop();

Console.WriteLine($"serialize time={sw.ElapsedMilliseconds}ms, size={buf.Length}");

sw.Restart();

var d2 = MessagePackSerializer.Deserialize<Cart>(buf);

sw.Stop();

Console.WriteLine($"deserialize time={sw.ElapsedMilliseconds}ms");

PrintCart(d2);

void PrintCart(Cart cart)
{
    Console.WriteLine($"----- Cart id={cart.Id} -----");

    foreach (var line in cart.Lines)
    {
        Console.WriteLine(line);
    }

    Console.WriteLine("-----");
}

[MessagePackObject(true)]
public record Cart(string Id, List<CartLine> Lines);

[MessagePackObject(true)]
public record CartLine(string ItemId, int Qty);
