using Microsoft.AspNetCore.HttpLogging;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddHttpLogging(o => {
    o.CombineLogs = true;
    o.LoggingFields = HttpLoggingFields.All;
});

var app = builder.Build();

app.UseHttpLogging();

app.MapPost("/create", (CreateItem input) => {
    var id = Guid.NewGuid().ToString();
    var item = new Item(id, input.Name, input.Value);

    Console.WriteLine(item);

    return Results.Ok(item);
});

app.Run();


record Item(string Id, string Name, int Value);
record CreateItem(string Name, int Value);
