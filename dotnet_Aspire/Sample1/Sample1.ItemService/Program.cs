var builder = WebApplication.CreateBuilder(args);

builder.AddServiceDefaults();

var app = builder.Build();

app.MapGet("/items", () => Enumerable.Range(1, 3).Select(i => new Item($"item-{i}", i * 10)).ToArray());

app.Run();

record Item(string Name, int Value);
