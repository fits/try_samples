using Microsoft.AspNetCore.HttpLogging;
using Microsoft.Extensions.Logging;
using Serilog;

var config = new ConfigurationBuilder()
    .SetBasePath(Directory.GetCurrentDirectory())
    .AddJsonFile("appsettings.json")
    .Build();

Log.Logger = new LoggerConfiguration()
    .ReadFrom.Configuration(config)
    .CreateLogger();

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSerilog();
builder.Services.AddHttpLogging();

builder.Services.AddSingleton<ItemService>();

var app = builder.Build();

app.UseSerilogRequestLogging();
app.UseHttpLogging();

app.MapPost("/create", (CreateItem input, ItemService service) => {
    var res = service.create(input.Name, input.Value);
    return Results.Ok(res);
});

app.Run();


record Item(string Id, string Name, int Value);
record CreateItem(string Name, int Value);

class Item2(string id, string name, int value)
{
    public string Id => id;
    public string Name => name;
    public int Value => value;
}

class ItemService(ILogger<ItemService> log)
{
    public (Item, Item2) create(string name, int value)
    {
        var id = Guid.NewGuid().ToString();
        var item = new Item(id, name, value);
        var item2 = new Item2(id, name, value);

        log.LogInformation("created item: {item}", item);
        log.LogInformation("created item2: {item2}", item2);

        try
        {
            fakeError();
        }
        catch (Exception ex)
        {
            log.LogError(ex, "test logging1");
            log.LogError("test logging2: {ex}", ex);
        }

        return (item, item2);
    }

    private void fakeError()
    {
        throw new Exception("fake error");
    }
}
