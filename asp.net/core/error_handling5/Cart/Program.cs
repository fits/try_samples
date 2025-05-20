using Microsoft.AspNetCore.HttpLogging;
using Microsoft.AspNetCore.Http.Json;
using Microsoft.Extensions.Options;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using Example.Core;

var builder = WebApplication.CreateBuilder(args);

var itemApiUri = builder.Configuration["ItemApiUri"] ?? throw new Exception("ItemApiUri is not set");

builder.Services.ConfigureHttpJsonOptions(o =>
{
    o.SerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.SnakeCaseLower;
});

builder.Services.AddHttpLogging(o =>
{
    o.CombineLogs = true;
    o.LoggingFields = HttpLoggingFields.All;
});

builder.Services.AddTransient<HttpClientErrorHandler>();

builder.Services.AddHttpClient<ItemService>(client =>
{
    client.BaseAddress = new Uri(itemApiUri);
})
.AddHttpMessageHandler<HttpClientErrorHandler>();

var app = builder.Build();

app.UseHttpLogging();
app.UseMiddleware<ErrorHandleMiddleware>();

app.MapPost("/additem", async (ItemService service, AddItem input) =>
{
    if (String.IsNullOrWhiteSpace(input.CartCode))
    {
        throw new InvalidInputException("cart_code", "cart_code must not be blank");
    }
    if (input.ItemId is null)
    {
        throw new InvalidInputException("item_id", "item_id requried");
    }

    var item = await service.FindAsync(input.ItemId);

    return new Cart(input.CartCode, new List<CartItem> { new(item, 1) });
});

app.Run();

record Cart(string Code, List<CartItem> Items);
record CartItem(Item Item, int Qty);
record Item(string Id, string Name, decimal Price);

record AddItem(string CartCode, string ItemId);
record FindItem(string ItemId);

class ItemService(HttpClient client, IOptions<JsonOptions> opts)
{
    public async Task<Item> FindAsync(string itemId)
    {
        var res = await client.PostAsJsonAsync<FindItem>("/find", new FindItem(itemId), opts.Value.SerializerOptions);
        var item = await res.Content.ReadFromJsonAsync<Item>();

        if (item is null || item.Id is null)
        {
            throw new ItemException($"invalid item: id={itemId}, item={item}");
        }

        return item;
    }
}

class InvalidInputException(string field, string message) : Exception(message), IAppInputError
{
    public string ErrorCode => "C001";
    public string FieldName => field;
}

class ItemException(string message) : Exception(message), IAppError
{
    public string ErrorCode => "C101";
}
