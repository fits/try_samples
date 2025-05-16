var builder = WebApplication.CreateBuilder(args);

builder.Services.AddHttpLogging(o => 
{
    o.CombineLogs = true;
});

var app = builder.Build();

app.UseHttpLogging();

app.Use(async (ctx, next) =>
{
    try
    {
        await next(ctx);
    }
    catch (Exception ex)
    {
        app.Logger.LogError(ex, "middleware error handled");

        if (ex is IInputError ie)
        {
            app.Logger.LogInformation($"handled InputError: {ie.Code}, {ie.Message}");

            ctx.Response.StatusCode = StatusCodes.Status400BadRequest;

            await ctx.Response.WriteAsJsonAsync(ie);
        }
        else if (ex is IAppError ae)
        {
            app.Logger.LogInformation($"handled AppError: {ae.Code} {ae.Message}");

            ctx.Response.StatusCode = StatusCodes.Status500InternalServerError;

            await ctx.Response.WriteAsJsonAsync(ae);
        }
        else
        {
            throw;
        }
    }
});

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

interface IAppError
{
    string Code { get; }
    string Message { get; }
}

interface IInputError : IAppError
{
    string Property { get; }
}

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
