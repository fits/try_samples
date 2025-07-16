var builder = WebApplication.CreateBuilder(args);

builder.Services.AddHttpLogging(o => 
{
    o.CombineLogs = true;
});

builder.Services.AddScoped<IContext, TestContext>();
builder.Services.AddScoped<IProcessor, TestProcessor>();

var app = builder.Build();

app.UseMiddleware<SampleMiddleware>();
app.UseHttpLogging();

app.MapGet("/", async (IProcessor proc) => {
    app.Logger.LogInformation("called /");

    await proc.Process("endpoint");

    return Results.Ok("root endpoint");
});

app.Run();

class SampleMiddleware(RequestDelegate next, ILogger<SampleMiddleware> logger, IProcessor proc)
{
    public async Task InvokeAsync(HttpContext ctx)
    {
        logger.LogInformation($"SampleMiddleware: this={this.GetType()}:{this.GetHashCode()}");

        await proc.Process("middleware");

        logger.LogInformation("before next call");

        await next(ctx);

        logger.LogInformation("after next call");
    }
}

interface IProcessor
{
    Task Process(string id);
}

class TestProcessor(IContext ctx, ILogger<TestProcessor> logger) : IProcessor
{
    public async Task Process(string id)
    {
        logger.LogInformation($"{id} TestProcessor.Check: this={this.GetType()}:{this.GetHashCode()}, ctx={ctx.GetType()}:{ctx.GetHashCode()}");
        await ctx.Show(id);
    }
}

interface IContext
{
    Task Show(string id);
}

class TestContext(ILogger<TestContext> logger) : IContext
{
    public async Task Show(string id)
    {
        await Task.Delay(1);
        logger.LogInformation($"{id} TestContext.Show: this={this.GetType()}:{this.GetHashCode()}");
    }
}
