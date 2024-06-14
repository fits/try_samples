var builder = WebApplication.CreateBuilder(args);

builder.AddServiceDefaults();

builder.Services.AddHttpClient<ItemApiClient>(client =>
{
    client.BaseAddress = new("https+http://itemservice");
});

var app = builder.Build();

app.MapGet("/info", async (ItemApiClient client) =>
{
    var items = await client.GetItemsAsync();
    return new DataInfo(items, DateTime.Now);
});

app.Run();

record DataInfo(Item[] Items, DateTime Timestamp);
record Item(string Name, int Value);

class ItemApiClient(HttpClient httpClient)
{
    public async Task<Item[]> GetItemsAsync(CancellationToken cancellationToken = default)
    {
        var items = await httpClient.GetFromJsonAsync<Item[]>("/items", cancellationToken);

        return items ?? [];
    }
}
