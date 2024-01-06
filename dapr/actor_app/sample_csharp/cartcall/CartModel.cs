using Dapr.Actors;
using System.Threading.Tasks;

public record Cart(string Id, CartLine[] Lines, decimal Total)
{
    public Cart() : this("", Array.Empty<CartLine>(), 0)
    {
    }
}

public record CartLine(string ItemId, decimal Price, int Qty)
{
    public CartLine() : this("", 0, 0)
    {
    }
}

public interface ICartActor : IActor
{
    Task<Cart> CreateAsync();
    Task<Cart> AddItemAsync(string itemId, decimal price, int qty);
    Task<Cart> GetAsync();
}
