using Dapr.Client;

var builder = WebApplication.CreateBuilder(args);

builder.AddServiceDefaults();

builder.Services.AddDaprClient();

var app = builder.Build();

app.MapGet("/info", async (DaprClient client) =>
{
    var items = await client.InvokeMethodAsync<Item[]>(HttpMethod.Get, "itemservice", "items");
    return new DataInfo(items ?? [], DateTime.Now);
});

app.Run();

record DataInfo(Item[] Items, DateTime Timestamp);
record Item(string Name, int Value);