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

class ErrorHandleMiddleware(RequestDelegate next, ILogger<ErrorHandleMiddleware> logger)
{
    public async Task InvokeAsync(HttpContext ctx)
    {
        try
        {
            await next(ctx);
        }
        catch (Exception ex)
        {
            logger.LogError(ex, "middleware error handled");

            var res = ex switch
            {
                IInputError i => await HandleInputErrorAsync(ctx, i),
                IAppError a => await HandleAppErrorAsync(ctx, a),
                _ => false,
            };

            if (!res)
            {
                throw;
            }
        }
    }

    private async ValueTask<bool> HandleInputErrorAsync(HttpContext ctx, IInputError ex)
    {
        logger.LogInformation($"handled InputError: {ex.Code}, {ex.Message}");

        ctx.Response.StatusCode = StatusCodes.Status400BadRequest;

        await ctx.Response.WriteAsJsonAsync(ex);

        return true;
    }

    private async ValueTask<bool> HandleAppErrorAsync(HttpContext ctx, IAppError ex)
    {
        logger.LogInformation($"handled AppError: {ex.Code} {ex.Message}");

        ctx.Response.StatusCode = StatusCodes.Status500InternalServerError;

        await ctx.Response.WriteAsJsonAsync(ex);

        return true;
    }
}

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
