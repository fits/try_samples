using Dapr.Actors;
using Dapr.Actors.Runtime;
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

internal class CartActor : Actor, ICartActor
{
    public CartActor(ActorHost host) : base(host)
    {
    }

    protected override Task OnActivateAsync()
    {
        Console.WriteLine($"{DateTime.UtcNow.ToString("o")} - activate: id={this.Id}");
        return Task.CompletedTask;
    }

    protected override Task OnDeactivateAsync()
    {
        Console.WriteLine($"{DateTime.UtcNow.ToString("o")} - deactivate: id={this.Id}");
        return Task.CompletedTask;
    }

    public async Task<Cart> CreateAsync()
    {
        Cart cart = emptyCart(this.Id.GetId());

        await this.StateManager.SetStateAsync<Cart>("state", cart);

        return cart;
    }

    public async Task<Cart> AddItemAsync(string itemId, decimal price, int qty)
    {
        var cart = await GetAsync();

        Cart newCart = addLine(cart, createLine(itemId, price, qty));

        await this.StateManager.SetStateAsync<Cart>("state", newCart);

        return newCart;
    }

    public Task<Cart> GetAsync()
    {
        return this.StateManager.GetStateAsync<Cart>("state");
    }

    private Cart emptyCart(string id)
    {
        return new()
        {
            Id = id
        };
    }

    private CartLine createLine(string itemId, decimal price, int qty)
    {
        return new(itemId, Math.Max(0, price), Math.Max(1, qty));
    }

    private Cart addLine(Cart cart, CartLine line)
    {
        CartLine[] lines = new CartLine[cart.Lines.Length + 1];

        cart.Lines.CopyTo(lines, 0);
        lines[lines.Length - 1] = line;

        decimal total = lines.Aggregate(0m, (acc, x) => acc + (x.Price * x.Qty));

        return new(cart.Id, lines, total);
    }
}
