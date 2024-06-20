using System.Text.Json.Serialization;

var builder = WebApplication.CreateSlimBuilder(args);

var store = new Dictionary<string, Item>();

builder.Services.ConfigureHttpJsonOptions(options =>
{
    options.SerializerOptions.TypeInfoResolverChain.Insert(0, AppJsonSerializerContext.Default);
});

var app = builder.Build();

app.MapPost("/create", (CreateItem input) =>
{
    var item = new Item(input.Id, input.Price);

    if (!store.TryAdd(item.Id, item))
    {
        return Results.Conflict();
    }

    return Results.Ok(item);
});

app.MapPost("/find", (FindItem input) =>
{
    Item? item;

    if (!store.TryGetValue(input.Id, out item))
    {
        return Results.NotFound();
    }

    return Results.Ok(item!);
});

app.MapGet("/items", () => store.Values.ToArray());

app.Run();

[JsonSerializable(typeof(Item[]))]
[JsonSerializable(typeof(CreateItem))]
[JsonSerializable(typeof(FindItem))]
internal partial class AppJsonSerializerContext : JsonSerializerContext
{
}

record CreateItem(string Id, int Price);
record FindItem(string Id);

record Item(string Id, int Price);
