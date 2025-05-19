var builder = WebApplication.CreateBuilder(args);

builder.Services.AddHttpLogging(o => 
{
    o.CombineLogs = true;
});

var app = builder.Build();

app.UseMiddleware<ErrorHandleMiddleware>();
app.UseHttpLogging();

var rnd = new Random();

app.MapPost("/create", (CreateItem input) => {
    if (String.IsNullOrWhiteSpace(input.Name))
    {
        throw new BlankNameException($"Name must not be blank. input Name is '{input.Name}'");
    }

    if (input.Value < 0)
    {
        throw new InvalidValueException($"Value must be '>= 0'. input Value is {input.Value}");
    }

    if (rnd.Next(10) < 2)
    {
        throw new AbortedException("random aborted");
    }

    var id = Guid.NewGuid().ToString();
    var item = new Item(id, input.Name, input.Value);

    app.Logger.LogInformation($"* created item: {item}");

    return Results.Ok(item);
});

app.Run();


record Item(string Id, string Name, int Value);
record CreateItem(string Name, int Value);

class BlankNameException(string message) : Exception(message), IInputError
{
    public string Code => "E001";
    public string Property => "Name";
}

class InvalidValueException(string message) : Exception(message), IInputError
{
    public string Code => "E002";
    public string Property => "Value";
}

class AbortedException(string message) : Exception(message), IAppError
{
    public string Code => "E099";
}
