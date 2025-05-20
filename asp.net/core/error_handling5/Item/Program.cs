using Microsoft.AspNetCore.HttpLogging;
using System.Text.Json;
using Example.Core;

var builder = WebApplication.CreateBuilder(args);

builder.Services.ConfigureHttpJsonOptions(o =>
{
    o.SerializerOptions.PropertyNamingPolicy = JsonNamingPolicy.SnakeCaseLower;
});

builder.Services.AddHttpLogging(o =>
{
    o.CombineLogs = true;
    o.LoggingFields = HttpLoggingFields.All;
});

var app = builder.Build();

app.UseHttpLogging();
app.UseMiddleware<ErrorHandleMiddleware>();

var rnd = new Random();

app.MapPost("/find", (FindItem input) =>
{
    if (String.IsNullOrWhiteSpace(input.ItemId))
    {
        throw new InvalidInputException("item_id", "item_id must not be blank");
    }

    if (rnd.Next(10) < 2)
    {
        throw new TestException("random aborted");
    }

    if (rnd.Next(10) < 1)
    {
        throw new ItemNotFoundException($"not found item: id={input.ItemId}");
    }

    var item = new Item(input.ItemId, "test item", rnd.Next(5000));

    app.Logger.LogInformation($"* created item: {item}");

    return Results.Ok(item);
});

app.Run();


record Item(string Id, string Name, decimal Price);
record FindItem(string ItemId);

class InvalidInputException(string field, string message) : Exception(message), IAppInputError
{
    public string ErrorCode => "I001";
    public string FieldName => field;
}

class ItemNotFoundException(string message) : Exception(message), IAppError
{
    public string ErrorCode => "I002";
}

class TestException(string message) : Exception(message), IAppError
{
    public string ErrorCode => "I999";
}
