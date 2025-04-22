using Microsoft.AspNetCore.HttpLogging;
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

builder.Services.AddHttpLogging(o => {
    o.CombineLogs = true;
    o.LoggingFields = HttpLoggingFields.All;
});

var app = builder.Build();

app.UseSerilogRequestLogging();
app.UseHttpLogging();

app.MapPost("/create", (CreateItem input) => {
    Log.Information("called create");

    var id = Guid.NewGuid().ToString();
    var item = new Item(id, input.Name, input.Value);

    Log.Information($"created item: {item}");

    return Results.Ok(item);
});

app.Run();


record Item(string Id, string Name, int Value);
record CreateItem(string Name, int Value);
