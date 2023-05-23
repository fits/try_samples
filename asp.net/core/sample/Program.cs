var builder = WebApplication.CreateBuilder(args);
var app = builder.Build();

app.MapPost("/create", (CreateItem input) => {
    var id = Guid.NewGuid().ToString();
    var item = new Item(id, input.Name, input.Value);

    Console.WriteLine(item);

    return Results.Ok(item);
});

app.Run();
