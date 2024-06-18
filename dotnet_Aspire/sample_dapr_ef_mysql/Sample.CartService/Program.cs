using Dapr.Client;

var storeName = Environment.GetEnvironmentVariable("STORE_NAME") ?? "statestore";
var firstETag = Environment.GetEnvironmentVariable("FIRST_ETAG") ?? "-1";

var builder = WebApplication.CreateBuilder(args);

builder.AddServiceDefaults();

builder.Services.AddDaprClient();

var app = builder.Build();

app.MapPost("/create", async (DaprClient client, CreateCart input) =>
{
    var cart = new Cart(input.CartId, []);

    var ok = await client.TrySaveStateAsync(storeName, cart.Id, cart, firstETag);

    if (!ok)
    {
        return Results.Conflict();
    }

    return Results.Ok(cart);
});

app.MapPost("/get", (DaprClient client, GetCart input) => client.GetStateAsync<Cart>(storeName, input.CartId));

app.MapPost("/update-qty", async (DaprClient client, UpdateQty input) =>
{
    if (input.Qty < 0)
    {
        return Results.BadRequest("Qty >= 0");
    }

    var (cart, etag) = await client.GetStateAndETagAsync<Cart>(storeName, input.CartId);

    if (String.IsNullOrEmpty(etag))
    {
        return Results.BadRequest($"not found cart: {input.CartId}");
    }

    var item = await client.InvokeMethodAsync<FindItem, Item>("itemservice", "find", new FindItem(input.ItemId));

    if (item == null)
    {
        return Results.BadRequest($"not found item: {input.ItemId}");
    }

    var newCart = cart.AddLine(new CartLine(item, input.Qty));

    var ok = await client.TrySaveStateAsync(storeName, newCart.Id, newCart, etag);

    if (!ok)
    {
        return Results.Conflict(etag);
    }

    return Results.Ok(newCart);
});

app.Run();

record CreateCart(string CartId);
record GetCart(string CartId);
record UpdateQty(string CartId, string ItemId, int Qty);

record FindItem(string Id);

record Item(string Id, int Price)
{
    public Item() : this("", 0)
    {
    }
}

record Cart(string Id, CartLine[] Lines)
{
    public Cart() : this("", Array.Empty<CartLine>())
    {
    }

    public Cart AddLine(CartLine line)
    {
        var newLines = new List<CartLine>();
        var upd = false;

        foreach (var x in Lines)
        {
            if (x.IsSame(line))
            {
                if (line.Qty > 0)
                {
                    newLines.Add(x with { Qty = line.Qty });
                }

                upd = true;
            }
            else
            {
                newLines.Add(x);
            }
        }

        if (!upd && line.Qty > 0)
        {
            newLines.Add(line);
        }

        return this with { Lines = newLines.ToArray() };
    }
}

record CartLine(Item Item, int Qty)
{
    public CartLine() : this(new Item(), 0)
    {
    }

    public bool IsSame(CartLine trg)
    {
        return Item.Id == trg.Item.Id;
    }
}
