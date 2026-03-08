using System.Reflection;

var svc = new ItemService();
var item = svc.Create("item-1");

Console.WriteLine($"result={item}");

Console.WriteLine("-----");

var asm = Assembly.GetExecutingAssembly();

Console.WriteLine($"assembly={asm}");

foreach (var t in asm.GetTypes())
{
    Console.WriteLine($"type={t.FullName}");
}


public record Item(String Name);

public interface IItemService
{
    Item Create(String name);
}

public class ItemService : IItemService
{
    public Item Create(String name)
    {
        return new Item(name);
    }
}
